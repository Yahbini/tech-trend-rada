package com.techtrend.streamprocessing.topology;

import com.techtrend.streamprocessing.model.MentionTechMatch;
import com.techtrend.streamprocessing.serde.TechUpdateSerdes;
import com.techtrend.streamprocessing.service.NewTechDetectionService;
import com.techtrendradar.avro.NewTechDetected;
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
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Joins each mention with its technology row, keeps only new technologies and publishes them to new-tech-alerts.
 * Left join keeps mentions whose technology is not in the KTable yet, since those are the candidates for an alert.
 */
@Configuration
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechDetectionTopology {
  SpecificAvroSerde<TechMention> techMentionValueSerde;
  SpecificAvroSerde<NewTechDetected> newTechDetectedValueSerde;
  TechUpdateSerdes techUpdateSerdes;
  NewTechDetectionService newTechDetectionService;

  @Bean
  public KStream<Long, NewTechDetected> newTechDetectionKStream(KStream<Long, TechMention> techMentionKStream,
        KTable<Long, GenericRecord> techUpdatesTable,
        @Value("${app.kafka.topics.new-tech-alerts}") String newTechAlertTopic) {
    // Serdes are required for the auto repartition of the re-keyed mention stream (no default serde configured);
    // a fixed name keeps that topic stable when later nodes change the topology.
    KStream<Long, NewTechDetected> newTechDetectedKStream = techMentionKStream.leftJoin(techUpdatesTable,
                                   MentionTechMatch::new,
                                   Joined.with(Serdes.Long(), techMentionValueSerde, techUpdateSerdes.getValueSerde())
                                         .withName("mention-tech-join"))
                             .peek((technologyId, match) -> log.debug(
                                   "join technology_id={} matched={} external_id={}",
                                   technologyId, match.isMatched(), match.getTechMention().getExternalId()))
                             .filter((techId, match) -> newTechDetectionService.isNewTechnology(match))
                             .mapValues(this::toNewTechDetected)
                             .peek((technologyId, event) -> log.debug("event {}", event));

    newTechDetectedKStream.to(newTechAlertTopic, Produced.with(Serdes.Long(), newTechDetectedValueSerde));
    return newTechDetectedKStream;
  }

  // canonical_name/category_id stay null when the mention beats its CDC insert; the alert consumer looks them up by id.
  private NewTechDetected toNewTechDetected(Long technologyId, MentionTechMatch match) {
    TechMention techMention = match.getTechMention();
    return NewTechDetected.newBuilder()
                          .setTechnologyId(technologyId)
                          .setCanonicalName(match.getCanonicalName())
                          .setCategoryId(match.getCategoryId())
                          .setSource(techMention.getSource())
                          .setUrl(techMention.getUrl())
                          .setExternalId(techMention.getExternalId())
                          .setDetectedAt(techMention.getCrawledAt())
                          .build();
  }
}
