package com.techtrend.streamprocessing.topology;

import com.techtrend.streamprocessing.model.TechTrendWindowResult;
import com.techtrend.streamprocessing.processor.MentionVelocityProcessorSupplier;
import com.techtrendradar.avro.MentionWindowStats;
import com.techtrendradar.avro.TechMention;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Windowed aggregation branch: mention count/velocity per technology → tech-trend-aggregated.
 * Groups by technology_id; canonical_name/category are resolved when publishing, not per mention.
 */
@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendAggregationTopology {
  SpecificAvroSerde<TechMention> techMentionValueSerde;

  @Bean
  public KGroupedStream<Long, TechMention> techMentionKGroupedStream(KStream<Long, TechMention> techMentionKStream) {
    // Re-keyed by flatMap, so grouping repartitions by technology_id; a fixed name keeps that topic stable.
    return techMentionKStream.groupByKey(Grouped.with(Serdes.Long(), techMentionValueSerde)
                                                .withName("mention-count-group"));
  }

  @Bean
  public TimeWindowedKStream<Long, TechMention> techMentionTimeWindowedKStream(
        KGroupedStream<Long, TechMention> techMentionKGroupedStream,
        @Value("${app.aggregation.window-size}") Duration windowSize,
        @Value("${app.aggregation.grace}") Duration grace) {
    return techMentionKGroupedStream.windowedBy(TimeWindows.ofSizeAndGrace(windowSize, grace));
  }

  @Bean
  public KTable<Windowed<Long>, MentionWindowStats> mentionCountKTable(
        TimeWindowedKStream<Long, TechMention> techMentionKStream,
        SpecificAvroSerde<MentionWindowStats> mentionWindowStatsValueSerde) {
    return techMentionKStream.aggregate(() -> MentionWindowStats.newBuilder().build(),
          (technologyId, mention, stats) -> {
            stats.setMentionCount(stats.getMentionCount() + 1);
            stats.getSourceCounts().merge(mention.getSource().toString(), 1L, Long::sum);
            return stats;
          },
          Materialized.<Long, MentionWindowStats, WindowStore<Bytes, byte[]>>as("mention-count-store")
                      .withKeySerde(Serdes.Long())
                      .withValueSerde(mentionWindowStatsValueSerde)
    );
  }

  @Bean
  public KTable<Windowed<Long>, MentionWindowStats> closedMentionCountKTable(
        @Qualifier("mentionCountKTable") KTable<Windowed<Long>, MentionWindowStats> mentionCountKTable) {
    // One final record per technology/window after end + grace, so velocity never sees partial counts.
    return mentionCountKTable.suppress(
          Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded())
                    .withName("mention-window-closed"));
  }

  @Bean
  public KStream<Windowed<Long>, MentionWindowStats> closedMentionCountKStream(
        @Qualifier("closedMentionCountKTable") KTable<Windowed<Long>, MentionWindowStats> closedMentionCountKTable) {
    return closedMentionCountKTable.toStream()
                                   .peek((window, stats) -> log.debug(
                                         "window closed technology_id={} start={} end={} count={} sources={}",
                                         window.key(), window.window().startTime(), window.window().endTime(),
                                         stats.getMentionCount(), stats.getSourceCounts()));
  }

  @Bean
  public KStream<Long, TechTrendWindowResult> techTrendWindowResultKStream(
        KStream<Windowed<Long>, MentionWindowStats> closedMentionCountKStream) {
    // POJO values have no serde: map to Avro before any repartitioning operation or to().
    return closedMentionCountKStream.process(new MentionVelocityProcessorSupplier(), Named.as("mention-velocity"));
  }
}
