package com.techtrend.streamprocessing.topology;

import com.techtrend.streamprocessing.model.MentionTechMatch;
import com.techtrend.streamprocessing.serde.TechUpdateSerdes;
import com.techtrend.streamprocessing.service.NewTechDetectionService;
import com.techtrendradar.avro.TechMention;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Joins each mention with its technology row. Left join keeps mentions whose technology
 * is not in the KTable yet, since those are the candidates for a new-tech alert.
 */
@Configuration
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechDetectionTopology {
  SpecificAvroSerde<TechMention> techMentionValueSerde;
  TechUpdateSerdes techUpdateSerdes;
  NewTechDetectionService newTechDetectionService;

  @Bean
  public KStream<Long, MentionTechMatch> newTechDetectionKStream(KStream<Long, TechMention> techMentionKStream,
        KTable<Long, GenericRecord> techUpdatesTable) {
    // Serdes are required for the auto repartition of the re-keyed mention stream (no default serde configured);
    // a fixed name keeps that topic stable when later nodes change the topology.
    return techMentionKStream.leftJoin(techUpdatesTable,
                                   MentionTechMatch::new,
                                   Joined.with(Serdes.Long(), techMentionValueSerde, techUpdateSerdes.getValueSerde())
                                         .withName("mention-tech-join"))
                             .peek((technologyId, match) -> log.debug(
                                   "join technology_id={} matched={} new={} external_id={}",
                                   technologyId, match.isMatched(), newTechDetectionService.isNewTechnology(match),
                                   match.getTechMention().getExternalId()));
  }
}
