package com.techtrend.crawlingest.service;

import com.techtrend.crawlingest.dto.request.CrawlWebhookRequest;
import com.techtrend.crawlingest.entities.CrawlInvalidRecord;
import com.techtrend.crawlingest.entities.RawCrawlLog;
import com.techtrend.crawlingest.entities.Source;
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
  // String -> key -> external_id
  KafkaTemplate<String, Object> kafkaTemplate;
  CrawlPayloadValidator validator;
  SourceRepository sourceRepository;
  RawCrawlLogRepository rawCrawlLogRepository;
  CrawlInvalidRecordRepository crawlInvalidRecordRepository;
  UnclassifiedMentionRepository  unclassifiedMentionRepository;
  TechAliasMatcher techAliasMatcher;
  TransactionTemplate transactionTemplate;

  /**
   * Entry point for one webhook payload: always logs raw, then stops at crawl_invalid_record
   * (bad payload) or unclassified_mention (no known technology in the title).
   */
  public void ingest(CrawlWebhookRequest request) {
    boolean publishable = Boolean.TRUE.equals(transactionTemplate.execute(status -> saveRawLog(request)));

    // Publish after commit so a slow/down Kafka never holds a DB connection or rolls back the raw log.
    if (publishable) {
      publishMention(request);
    }
  }

  private boolean saveRawLog(CrawlWebhookRequest request) {
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
      return false;
    }

    List<String> technologies = techAliasMatcher.findCanonicalNames(request.getTitle());
    if (technologies.isEmpty()) {
      UnclassifiedMention unclassified = new UnclassifiedMention();
      unclassified.setRawCrawlLog(rawLog);
      unclassified.setRawText(request.getRawText());
      unclassified.setCreatedAt(Instant.now());
      unclassifiedMentionRepository.save(unclassified);
      log.info("Unclassified mention, raw_crawl_log_id={}, title={}", rawLog.getId(), request.getTitle());
      return false;
    }

    log.info("Classified mention, raw_crawl_log_id={}, technologies={}", rawLog.getId(), technologies);
    return true;
  }

  private void publishMention(CrawlWebhookRequest request) {
    Instant crawledAt = Instant.parse(request.getCrawledAt());

    TechMention mention = TechMention.newBuilder()
                                     .setSource(request.getSource())
                                     .setExternalId(request.getExternalId())
                                     .setRawText(request.getRawText())
                                     .setUrl(request.getUrl())
                                     .setCrawledAt(crawledAt)
                                     .build();

    kafkaTemplate.send("tech-mentions", request.getExternalId(), mention);
    log.info("Published mention to tech-mentions: external_id={}", request.getExternalId());
  }
}
