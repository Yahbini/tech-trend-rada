package com.techtrend.trendscoring.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Weights of the trend score; keep the sum at 1 so the score stays in [0, 1].
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "app.scoring.weights")
public class ScoringWeightsProperties {
  double velocity = 0.5;
  double sourceCount = 0.3;
  double categoryWeight = 0.2;
}
