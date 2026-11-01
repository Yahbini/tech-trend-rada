package com.techtrend.common.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * One document of index tech-trend-aggregated (1 technology × 1 closed 1h window); fields must match the strict mapping.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechTrendDocument {
  @JsonProperty("technology_id")
  Long technologyId;

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
  Long mentionCount;

  @JsonProperty("mention_velocity")
  Double mentionVelocity;


  @JsonProperty("source_counts")
  Map<String, Long> sourceCounts;
}
