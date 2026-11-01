package com.techtrend.essinkconsumerservice.config;

import com.techtrend.essinkconsumerservice.exception.NonRetryableIndexException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * Transient failures (ES/MySQL down, 429, 5xx) retry forever so no window is lost; rejected documents are logged and skipped.
 */
@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

  @Bean
  public CommonErrorHandler kafkaErrorHandler() {
    ExponentialBackOff backOff = new ExponentialBackOff(1_000, 2.0);
    backOff.setMaxInterval(30_000);

    // Recoverer runs once per given-up record; the offset is committed afterwards. DLQ goes here in phase 3.
    DefaultErrorHandler handler = new DefaultErrorHandler(
          (record, ex) -> log.error("Skip tech-trend record, not indexed: topic={} offset={} technologyId={}",
                record.topic(), record.offset(), record.key(), ex), backOff);
    handler.addNotRetryableExceptions(NonRetryableIndexException.class);
    handler.setCommitRecovered(true);

    // Without this, a long ES outage looks like a silent hang; root cause only, no repeated stack traces.
    handler.setRetryListeners(((record, ex, attempt) -> log.warn(
          "Index tech-trend record failed: offset={} technologyId={} attempt={} cause={}",
          record.offset(), record.key(), attempt, NestedExceptionUtils.getMostSpecificCause(ex).toString())));
    return handler;
  }

}
