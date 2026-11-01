package com.techtrend.telegrambotservice.consumer;

import com.techtrend.telegrambotservice.client.TelegramErrors;
import com.techtrend.telegrambotservice.service.NewTechAlertService;
import com.techtrendradar.avro.NewTechDetected;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * Consumes new-tech-alerts (key = technology_id); offset is committed only after the alert is fully handled.
 */
@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechAlertListener {
  NewTechAlertService newTechAlertService;

  // Ack only after ES accepted the doc; failures propagate so the error handler re-seeks this offset.
  @KafkaListener(topics = "${app.kafka.topics.new-tech-alerts}", groupId = "${app.kafka.groups.alert}")
  public void onMessage(ConsumerRecord<Long, NewTechDetected> record, Acknowledgment ack) {
    try {
      newTechAlertService.handle(record.value());
      ack.acknowledge();
    } catch (RestClientException e) {
      // Raw message may contain the bot URL with token → log only the safe description, no stack trace.
      log.error("Send new-tech alert failed, offset not committed: technologyId={} offset={} cause={}",
            record.key(), record.offset(), TelegramErrors.describe(e));
    } catch (Exception e) {
      log.error("Handle new-tech alert failed, offset not committed: technologyId={}", record.key(), e);
    }
  }
}
