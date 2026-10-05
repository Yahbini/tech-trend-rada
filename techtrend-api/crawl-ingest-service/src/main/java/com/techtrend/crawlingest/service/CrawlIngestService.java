package com.techtrend.crawlingest.service;

import com.techtrend.crawlingest.dto.request.CrawlWebhookRequest;
import com.techtrend.crawlingest.entities.CrawlInvalidRecord;
import com.techtrend.crawlingest.entities.RawCrawlLog;
import com.techtrend.crawlingest.entities.Source;
import com.techtrend.crawlingest.entities.Technology;
import com.techtrend.crawlingest.entities.UnclassifiedMention;
import com.techtrend.crawlingest.matcher.TechAliasMatcher;
import com.techtrend.crawlingest.repository.CrawlInvalidRecordRepository;
import com.techtrend.crawlingest.repository.RawCrawlLogRepository;
import com.techtrend.crawlingest.repository.SourceRepository;
import com.techtrend.crawlingest.repository.UnclassifiedMentionRepository;
import com.techtrend.crawlingest.validator.CrawlPayloadValidator;
import com.techtrendradar.avro.TechMention;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CrawlIngestService {
  static int MAX_SAVE_ATTEMPTS = 3;
  // String -> key -> external_id
  KafkaTemplate<String, Object> kafkaTemplate;
  CrawlPayloadValidator validator;
  SourceRepository sourceRepository;
  RawCrawlLogRepository rawCrawlLogRepository;
  CrawlInvalidRecordRepository crawlInvalidRecordRepository;
  UnclassifiedMentionRepository unclassifiedMentionRepository;
  TechAliasMatcher techAliasMatcher;
  TransactionTemplate transactionTemplate;
  TechnologyService technologyService;

  /**
   * Entry point for one webhook payload: always logs raw, then stops at crawl_invalid_record
   * (bad payload), unclassified_mention (no known technology) or a re-crawl already counted.
   */
  public void ingest(CrawlWebhookRequest request) {
    List<Long> countedIds = saveWithRetry(request);

    // Publish after commit so a slow/down Kafka never holds a DB connection or rolls back the raw log.
    if (!countedIds.isEmpty()) {
      publishMention(request, countedIds);
    }
  }

  // Ids of technologies counted by this payload; empty means nothing to publish.
  private List<Long> saveRawLog(CrawlWebhookRequest request) {
    List<String> errors = new ArrayList<>(validator.validateRequiredFields(request));
    errors.addAll(validator.validateFormat(request));

    Source source = null;
    if (request.getSource() != null && !request.getSource().isBlank()) {
      // request.getSource(n8n) = code(Source entity)
      source = sourceRepository.findByCode(request.getSource()).orElse(null);
      if (source == null) {
        errors.add("source '" + request.getSource() + "' is not registered");
      }
    }

    RawCrawlLog rawLog = new RawCrawlLog();
    rawLog.setSource(source);
    rawLog.setExternalId(request.getExternalId());
    rawLog.setRawPayload(request);
    rawLog.setCrawledAt(errors.isEmpty() ? Instant.parse(request.getCrawledAt()) : null);
    rawCrawlLogRepository.save(rawLog);

    if (!errors.isEmpty()) {
      CrawlInvalidRecord invalidRecord = new CrawlInvalidRecord();
      invalidRecord.setRawCrawlLog(rawLog);
      invalidRecord.setReason(String.join("; ", errors));
      crawlInvalidRecordRepository.save(invalidRecord);
      log.warn("Invalid crawl payload, raw_crawl_log_id={}, reasons={}", rawLog.getId(), errors);
      return List.of();
    }

    List<String> technologies = techAliasMatcher.findCanonicalNames(request.getTitle());
    if (technologies.isEmpty()) {
      UnclassifiedMention unclassified = new UnclassifiedMention();
      unclassified.setRawCrawlLog(rawLog);
      unclassified.setRawText(request.getRawText());
      unclassified.setCreatedAt(Instant.now());
      unclassifiedMentionRepository.save(unclassified);
      log.info("Unclassified mention, raw_crawl_log_id={}, title={}", rawLog.getId(), request.getTitle());
      return List.of();
    }

    log.info("Classified mention, raw_crawl_log_id={}, technologies={}", rawLog.getId(), technologies);
    List<Long> countedIds = new ArrayList<>();

    // Lock rows in a fixed (alphabetical) order so two posts naming the same technologies cannot deadlock.
    for (String canonicalName : technologies.stream().sorted().toList()) {
      Technology technology = technologyService.findOrNew(canonicalName);
      if (technologyService.isAlreadyCounted(technology, rawLog)) {
        continue;
      }
      technology.increaseMentionCount();
      technologyService.calculateMentionCountLast7d(technology);
      technologyService.calculateMentionVelocity(technology);
      technologyService.assignCategory(technology);
      technologyService.assignIsNew(technology);
      technologyService.upsert(technology);
      technologyService.recordMention(technology, rawLog);
      countedIds.add(technology.getId());
    }
    return countedIds;
  }

  /**
   * Concurrent webhooks can deadlock or race on inserting the same new technology (UNIQUE canonical_name);
   * the loser is rolled back and retried in a fresh transaction, where it sees the committed row.
   */
  private List<Long> saveWithRetry(CrawlWebhookRequest request) {
    for (int attempt = 1; ; attempt++) {
      try {
        List<Long> countedIds = transactionTemplate.execute(status -> saveRawLog(request));
        return countedIds != null ? countedIds : List.of();
      } catch (PessimisticLockingFailureException | DataIntegrityViolationException e) {
        if (attempt >= MAX_SAVE_ATTEMPTS) {
          throw e;
        }
        log.warn("Retry saving crawl payload, external_id={}, attempt={}, cause={}",
              request.getExternalId(), attempt, e.getClass().getSimpleName());
      }
    }
  }

  private void publishMention(CrawlWebhookRequest request, List<Long> technologyIds) {
    Instant crawledAt = Instant.parse(request.getCrawledAt());

    TechMention mention = TechMention.newBuilder()
                                     .setSource(request.getSource())
                                     .setExternalId(request.getExternalId())
                                     .setRawText(request.getRawText())
                                     .setUrl(request.getUrl())
                                     .setCrawledAt(crawledAt)
                                     .setTechnologyIds(technologyIds)
                                     .build();

    kafkaTemplate.send("tech-mentions", request.getExternalId(), mention);
    log.info("Published mention to tech-mentions: external_id={}, technology_ids={}", request.getExternalId(),
          technologyIds);
  }
}
