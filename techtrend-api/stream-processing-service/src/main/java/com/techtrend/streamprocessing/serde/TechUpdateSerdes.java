package com.techtrend.streamprocessing.serde;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Key/value Serde pair for tech-updates, grouped so callers inject one bean instead of two of the same type.
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechUpdateSerdes {
  GenericAvroSerde keySerde;
  GenericAvroSerde valueSerde;
}
