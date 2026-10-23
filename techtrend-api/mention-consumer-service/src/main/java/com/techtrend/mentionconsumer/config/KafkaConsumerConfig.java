package com.techtrend.mentionconsumer.config;

import com.techtrendradar.avro.TechMention;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.properties.schema.registry.url}")
  private String schemaRegistryUrl;

  @Bean
  public ConsumerFactory<String, TechMention> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put("schema.registry.url", schemaRegistryUrl);
    props.put("specific.avro.reader", true);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, TechMention> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TechMention> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    // Self-built factory: Boot does not attach CommonErrorHandler beans here, so it must be set by hand.
    factory.setCommonErrorHandler(kafkaErrorHandler());
    return factory;
  }

  /**
   * Cassandra failures retry forever with backoff so no mention is lost; records the handler gives up on are logged and committed.
   */
  @Bean
  public CommonErrorHandler kafkaErrorHandler() {
    ExponentialBackOff backOff = new ExponentialBackOff(1_000, 2.0);
    backOff.setMaxInterval(30_000);

    // Only non-retryable records (e.g. conversion errors) reach here; DLQ goes here in phase 3.
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
          (record, ex) -> log.error("Skip mention record, not saved: topic={} offset={} externalId={}", record.topic(),
                record.offset(), record.key(), ex), backOff);
    errorHandler.setCommitRecovered(true);

    // Without this, a long Cassandra outage looks like a silent hang; root cause only, no repeated stack traces.
    errorHandler.setRetryListeners((record, ex, attempt) -> log.warn(
          "Save mention failed, retrying: offset={} externalId={} attempt={} cause={}", record.offset(), record.key(),
          attempt, NestedExceptionUtils.getMostSpecificCause(ex).toString()));
    return errorHandler;
  }
}
