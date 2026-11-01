package com.techtrend.essinkconsumerservice.consumer;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.JsonpUtils;
import com.techtrend.common.document.TechTrendDocument;
import com.techtrend.essinkconsumerservice.mapper.TechTrendDocumentMapper;
import com.techtrend.essinkconsumerservice.service.TechTrendDocumentService;
import com.techtrend.essinkconsumerservice.service.TechTrendIndexService;
import com.techtrendradar.avro.TechTrendAggregated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Consumes tech-trend-aggregated (key = technology_id, 1 record per technology per closed 1h window).
 */
@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendAggregatedListener {
  TechTrendDocumentService techTrendDocumentService;
  TechTrendDocumentMapper documentMapper;
  TechTrendIndexService techTrendIndexService;
  JsonpMapper jsonpMapper;

  // Ack only after ES accepted the doc; failures propagate so the error handler re-seeks this offset.
  @KafkaListener(topics = "${app.kafka.topics.tech-trend-aggregated}", groupId = "${app.kafka.groups.es-sink}")
  public void onMessage(ConsumerRecord<Long, TechTrendAggregated> record, Acknowledgment ack) {
    TechTrendDocument document = techTrendDocumentService.toDocument(record.value());
    String documentId = documentMapper.documentIdOf(record.value());
    log.debug("Mapped tech-trend document: offset={} id={} json={}",
          record.offset(), documentId, JsonpUtils.toJsonString(document, jsonpMapper));
    techTrendIndexService.index(documentId, document);
    ack.acknowledge();
  }
}
