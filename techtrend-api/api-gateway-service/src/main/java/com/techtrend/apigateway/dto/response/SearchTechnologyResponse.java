package com.techtrend.apigateway.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * One matched technology of GET /search; windowStart tells which 1h window the counts come from (may differ per item).
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchTechnologyResponse {
  Long technologyId;
  String canonicalName;
  String category;
  Instant windowStart;
  Long mentionCount;
  Double mentionVelocity;
}
