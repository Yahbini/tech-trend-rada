package com.techtrend.essinkconsumerservice.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * One document of index tech-trend-aggregated (1 technology × 1 closed 1h window); fields must match the strict mapping.
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendDocument {
  @JsonProperty("technology_id")
  long technologyId;

  @JsonProperty("canonical_name")
  String canonicalName;

  @JsonProperty("category_id")
  Long categoryId;

  @JsonProperty("category")
  String category;

  // ISO-8601 UTC string, independent of how the ES client's Jackson mapper serializes Instant.
  @JsonProperty("window_start")
  String windowStart;

  @JsonProperty("window_end")
  String windowEnd;

  @JsonProperty("mention_count")
  long mentionCount;

  @JsonProperty("mention_velocity")
  double mentionVelocity;

  @JsonProperty("source_counts")
  Map<String, Long> sourceCounts;
}
