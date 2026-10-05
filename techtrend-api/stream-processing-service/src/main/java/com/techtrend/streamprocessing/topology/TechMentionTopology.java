package com.techtrend.streamprocessing.topology;

import com.techtrendradar.avro.TechMention;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KStream of classified mentions from tech-mentions, re-keyed from external_id to technology_id
 * (one record per technology) so it can join the technology KTable.
 */
@Configuration
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechMentionTopology {
  SpecificAvroSerde<TechMention> techMentionValueSerde;

  @Bean
  public KStream<Long, TechMention> techMentionKStream(StreamsBuilder builder,
        @Value("${app.kafka.topics.tech-mentions}") String techMentionTopic) {
    // Messages published before technology_ids existed read as [] and yield no record.
    return builder.stream(techMentionTopic, Consumed.with(Serdes.String(), techMentionValueSerde))
                  .flatMap((externalId, mention) -> mention.getTechnologyIds().stream()
                                                           .map(technologyId -> KeyValue.pair(technologyId, mention))
                                                           .toList())
                  // Per-record trace, off by default; enable with --logging.level.<this class>=DEBUG.
                  .peek((technologyId, mention) -> log.debug("mention technology_id={} external_id={} source={}",
                                                             technologyId, mention.getExternalId(), mention.getSource()));
  }
}
