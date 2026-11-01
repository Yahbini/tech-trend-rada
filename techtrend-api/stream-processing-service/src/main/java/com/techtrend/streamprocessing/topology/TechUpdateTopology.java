package com.techtrend.streamprocessing.topology;

import com.techtrend.streamprocessing.serde.TechUpdateSerdes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KTable holding the latest technology row per key, read from the Debezium CDC topic tech-updates (unwrapped).
 * Exposed as a bean so the mention join can reuse it.
 */
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechUpdateTopology {
  public static final String TECH_UPDATES_STORE = "tech-updates-store";

  TechUpdateSerdes techUpdateSerdes;

  @Bean
  KTable<Long, GenericRecord> techUpdatesTable(StreamsBuilder builder, @Value("${app.kafka.topics.tech-updates}") String techUpdateTopic) {
    // Re-key Debezium's Avro {id} to a plain Long so mentions can join on technology_id;
    // the key is derived from the record key, so tombstones keep it and delete rows correctly.
    return builder
          .stream(techUpdateTopic, Consumed.with(techUpdateSerdes.getKeySerde(), techUpdateSerdes.getValueSerde()))
          .selectKey((key, value) -> (Long) key.get("id"))
          .toTable(Materialized.<Long, GenericRecord, KeyValueStore<Bytes, byte[]>>as(TECH_UPDATES_STORE)
                               .withKeySerde(Serdes.Long())
                               .withValueSerde(techUpdateSerdes.getValueSerde()));
  }
}
