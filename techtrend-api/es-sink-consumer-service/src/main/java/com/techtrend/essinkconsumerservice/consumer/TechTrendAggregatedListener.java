package com.techtrend.essinkconsumerservice.consumer;

import com.techtrendradar.avro.TechTrendAggregated;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes tech-trend-aggregated (key = technology_id, 1 record per technology per closed 1h window).
 */
@Slf4j
@Component
public class TechTrendAggregatedListener {

  // No ack yet: offset stays uncommitted so the same records replay while building index logic
  @KafkaListener(topics = "${app.kafka.topics.tech-trend-aggregated}", groupId = "${app.kafka.groups.es-sink}")
  public void onMessage(ConsumerRecord<Long, TechTrendAggregated> record) {
    TechTrendAggregated value = record.value();
    log.info("Received tech-trend-aggregated: partition={} offset={} technologyId={} name={} categoryId={} window=[{} - {}] count={} velocity={} sources={}",
          record.partition(), record.offset(), record.key(),
          value.getCanonicalName(), value.getCategoryId(),
          value.getWindowStart(), value.getWindowEnd(),
          value.getMentionCount(), value.getMentionVelocity(), value.getSourceCounts());
  }
}
