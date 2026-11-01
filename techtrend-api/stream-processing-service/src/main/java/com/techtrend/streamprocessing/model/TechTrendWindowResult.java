package com.techtrend.streamprocessing.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

/**
 * Internal hourly aggregation result for one technology.
 * Not published as-is: mapped to the tech-trend-aggregated Avro record when publishing.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendWindowResult {
  long technologyId;
  String canonicalName;
  Long categoryId;
  Instant windowStart;
  Instant windowEnd;
  long mentionCount;
  Map<String, Long> sourceCounts;
  Double mentionVelocity;

  @Builder
  private TechTrendWindowResult(long technologyId, String canonicalName, Long categoryId, Instant windowStart,
        Instant windowEnd, long mentionCount, Map<String, Long> sourceCounts, Double mentionVelocity) {
    this.technologyId = technologyId;
    this.canonicalName = canonicalName;
    this.categoryId = categoryId;
    this.windowStart = windowStart;
    this.windowEnd = windowEnd;
    this.mentionCount = mentionCount;
    // Copy: the store's MentionWindowStats map is mutated in place by the aggregator.
    this.sourceCounts = Map.copyOf(sourceCounts);
    this.mentionVelocity = mentionVelocity;
  }
}
