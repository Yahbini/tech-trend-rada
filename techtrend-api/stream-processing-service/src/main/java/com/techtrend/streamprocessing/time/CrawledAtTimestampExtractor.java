package com.techtrend.streamprocessing.time;

import com.techtrendradar.avro.TechMention;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * Event time for tech-mentions = crawled_at, so windows count mentions by crawl time even on retry/replay.
 */
public class CrawledAtTimestampExtractor implements TimestampExtractor {

  @Override
  public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
    // Fall back to the publish time: throwing kills the stream thread and a negative timestamp drops the record.
    if (record.value() instanceof TechMention mention && mention.getCrawledAt() != null) {
      return mention.getCrawledAt().toEpochMilli();
    }
    return record.timestamp();
  }
}
