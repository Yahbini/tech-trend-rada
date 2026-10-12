package com.techtrend.streamprocessing.serde;

import com.techtrendradar.avro.MentionWindowStats;
import com.techtrendradar.avro.NewTechDetected;
import com.techtrendradar.avro.TechMention;
import com.techtrendradar.avro.TechTrendAggregated;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Avro Serdes for the topology's topics: tech-mentions and tech-updates (input),
 * new-tech-alerts and tech-trend-aggregated (output).
 */
@Configuration
public class AvroSerdeConfig {
  @Value("${spring.kafka.streams.properties.schema.registry.url}")
  private String schemaRegistryUrl;

  @Bean
  public SpecificAvroSerde<TechMention> techMentionValueSerde() {
    SpecificAvroSerde<TechMention> serde = new SpecificAvroSerde<>();
    serde.configure(registryConfig(), false);
    return serde;
  }

  @Bean
  public SpecificAvroSerde<NewTechDetected> newTechDetectedValueSerde() {
    SpecificAvroSerde<NewTechDetected> serde = new SpecificAvroSerde<>();
    serde.configure(registryConfig(), false);
    return serde;
  }

  @Bean
  public SpecificAvroSerde<MentionWindowStats> mentionWindowStatsValueSerde() {
    SpecificAvroSerde<MentionWindowStats> serde = new SpecificAvroSerde<>();
    serde.configure(registryConfig(), false);
    return serde;
  }

  @Bean
  public SpecificAvroSerde<TechTrendAggregated> techTrendAggregatedValueSerde() {
    SpecificAvroSerde<TechTrendAggregated> serde = new SpecificAvroSerde<>();
    serde.configure(registryConfig(), false);
    return serde;
  }

  // Generic because Debezium auto-registers the tech-updates schema; there is no generated class.
  @Bean
  public TechUpdateSerdes techUpdateSerdes() {
    return new TechUpdateSerdes(genericAvroSerde(true), genericAvroSerde(false));
  }

  private GenericAvroSerde genericAvroSerde(boolean isKey) {
    GenericAvroSerde serde = new GenericAvroSerde();
    serde.configure(registryConfig(), isKey);
    return serde;
  }

  private Map<String, String> registryConfig() {
    return Map.of("schema.registry.url", schemaRegistryUrl);
  }
}
