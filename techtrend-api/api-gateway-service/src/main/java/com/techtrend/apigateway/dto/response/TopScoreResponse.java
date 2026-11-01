package com.techtrend.apigateway.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * One ranked technology of GET /top: 7-day score plus the three inputs that explain it.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopScoreResponse {
  Long technologyId;
  String canonicalName;
  String category;
  Double score;
  Double mentionVelocity;
  Long sourceCount;
  Double categoryWeight;
  Integer rank;
}
