package com.techtrend.trendscoring.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechnologyScoreInput {
  Long technologyId;
  String canonicalName;
  String categoryCode;
  BigDecimal mentionVelocity;
  Long sourceCount;
  BigDecimal categoryWeight;
}