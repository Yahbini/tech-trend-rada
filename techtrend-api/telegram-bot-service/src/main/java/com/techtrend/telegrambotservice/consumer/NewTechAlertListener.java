package com.techtrend.telegrambotservice.consumer;

import com.techtrend.telegrambotservice.formatter.NewTechAlertFormatter;
import com.techtrend.telegrambotservice.mapper.NewTechAlertMapper;
import com.techtrendradar.avro.NewTechDetected;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechAlertListener {
  NewTechAlertFormatter newTechAlertFormatter;
  NewTechAlertMapper newTechAlertMapper;

  @KafkaListener(topics = "${app.kafka.topics.new-tech-alerts}", groupId = "${app.kafka.groups.alert}")
  public void onMessage(ConsumerRecord<Long, NewTechDetected> record, Acknowledgment ack) {
    try {
      String text = newTechAlertFormatter.format(newTechAlertMapper.toDto(record.value()));
      log.info("Received new-tech alert technologyId={} partition={} offset={}:\n{}",
            record.key(), record.partition(), record.offset(), text);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("Handle new-tech alert failed, offset not committed: technologyId={}", record.key(), e);
    }
  }
}
