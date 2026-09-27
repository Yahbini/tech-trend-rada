package com.techtrend.crawlingest.service;

import com.techtrendradar.avro.TechMention;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class CrawlIngestService {
    // String -> key -> external_id
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CrawlIngestService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMention(Map<String, Object> payload) {
        Instant crawledAt = Instant.parse((String) payload.get("crawled_at"));

        TechMention mention = TechMention.newBuilder()
                .setSource((String) payload.get("source"))
                .setExternalId((String) payload.get("external_id"))
                .setRawText((String) payload.get("raw_text"))
                .setUrl((String) payload.get("url"))
                .setCrawledAt(crawledAt)
                .build();

        kafkaTemplate.send("tech-mentions", mention.getExternalId().toString(), mention);
        log.info("Published mention to tech-mentions: external_id={}", mention.getExternalId());
    }
}
