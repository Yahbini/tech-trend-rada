package com.techtrend.trendscoring.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechnologyScore {
  Long technologyId;
  String canonicalName;
  String categoryCode;
  double score;
  double mentionVelocity;
  long sourceCount;
  double categoryWeight;
}
