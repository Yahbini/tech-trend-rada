package com.techtrend.mentionconsumer.service;

import com.techtrend.mentionconsumer.mapper.MentionRowMapper;
import com.techtrend.mentionconsumer.repository.MentionByTechRepository;
import com.techtrend.mentionconsumer.repository.MentionRepository;
import com.techtrendradar.avro.TechMention;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MentionConsumerListener {
    MentionRowMapper mapper;
    MentionRepository repository;
    MentionByTechRepository mentionByTechRepository;

    /**
     * Writes one by_source row plus one by_tech row per technology_id, then acks.
     * No try/catch: a Cassandra failure must reach the container error handler, otherwise the next ack skips this offset.
     */
    @KafkaListener(topics = "tech-mentions", groupId = "${app.kafka.groups.mention}",  containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, TechMention> record, Acknowledgment ack) {
        var mention = record.value();
        var row = mapper.map(mention);
        var byTechRows = mapper.mapByTech(mention);

        repository.insert(row);
        mentionByTechRepository.insertAll(byTechRows);
        ack.acknowledge();
        log.info("Saved mention: externalId={} source={} technologies={}", row.externalId(), row.source(), byTechRows.size());
    }
}
