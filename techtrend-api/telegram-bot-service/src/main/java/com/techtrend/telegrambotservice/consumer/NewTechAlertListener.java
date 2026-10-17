package com.techtrend.telegrambotservice.consumer;

import com.techtrendradar.avro.NewTechDetected;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Consumes new-tech-alerts (key = technology_id); offset is committed only after the alert is fully handled.
 */
@Slf4j
@Component
public class NewTechAlertListener {
  @KafkaListener(topics = "${app.kafka.topics.new-tech-alerts}", groupId = "${app.kafka.groups.alert}")
  public void onMessage(ConsumerRecord<Long, NewTechDetected> record, Acknowledgment ack) {
    try {
      log.info("Received new-tech alert technologyId={} partition={} offset={} value={}",
            record.key(), record.partition(), record.offset(), record.value());
      ack.acknowledge();
    } catch (Exception e) {
      log.error("Handle new-tech alert failed, offset not committed: technologyId={}", record.key(), e);
    }
  }
}
