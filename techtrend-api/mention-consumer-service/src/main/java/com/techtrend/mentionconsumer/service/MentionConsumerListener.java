package com.techtrend.mentionconsumer.service;

import com.techtrend.mentionconsumer.mapper.MentionRowMapper;
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

    /*
    * ConsumerRecord<String, TechMention> key -> from kafka topic -> external_id
    * */
    @KafkaListener(topics = "tech-mentions", groupId = "${app.kafka.groups.mention}",  containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, TechMention> record, Acknowledgment ack) {
        try {
            var row = mapper.map(record.value());
            repository.insert(row);
            ack.acknowledge();
            log.info("Saved mention from source = {}", row.source());
        } catch (Exception e) {
            log.error("Insert Cassandra error, no commit offset: {}", record.value(), e);
        }
    }
}
