package com.techtrend.apigateway.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * One ranked technology of GET /top; windowStart tells which 1h window the ranking belongs to.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopTechnologyResponse {
  Long technologyId;
  String canonicalName;
  String category;
  Instant windowStart;
  Long mentionCount;
  Double mentionVelocity;
  Integer rank;
}
