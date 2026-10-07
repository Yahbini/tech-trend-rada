package com.techtrend.streamprocessing.processor;

import com.techtrend.streamprocessing.model.TechTrendWindowResult;
import com.techtrendradar.avro.MentionWindowStats;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Set;

/**
 * Supplies the velocity processor together with prev-count-store, so the store is connected automatically.
 */
public class MentionVelocityProcessorSupplier implements ProcessorSupplier<Windowed<Long>, MentionWindowStats, Long,
      TechTrendWindowResult> {
  public static final String PREV_COUNT_STORE = "prev-count-store";

  @Override
  public Processor<Windowed<Long>, MentionWindowStats, Long, TechTrendWindowResult> get() {
    return new MentionVelocityProcessor();
  }

  @Override
  public Set<StoreBuilder<?>> stores() {
    // technology_id → last closed window's count; the timestamp slot holds that window's start.
    return Set.of(Stores.timestampedKeyValueStoreBuilder(
          Stores.persistentTimestampedKeyValueStore(PREV_COUNT_STORE), Serdes.Long(), Serdes.Long()));
  }
}
