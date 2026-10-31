package com.techtrend.trendscoring.service;

import com.techtrend.trendscoring.config.ScoringWeightsProperties;
import com.techtrend.trendscoring.dto.TechnologyScoreInput;
import com.techtrend.trendscoring.model.TechnologyScore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.DoubleSummaryStatistics;
import java.util.List;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreCalculator {
  ScoringWeightsProperties weightsProperties;

  // max = min → 0, avoids dividing by zero when every technology has the same value.
  private static double norm(double value, DoubleSummaryStatistics stats) {
    double range = stats.getMax() - stats.getMin();
    return range == 0 ? 0 : (value - stats.getMin()) / range;
  }

  /**
   * Min–max is taken over the given list, so pass every technology even when scoring one.
   */
  public List<TechnologyScore> calculate(List<TechnologyScoreInput> inputs) {
    DoubleSummaryStatistics velocityStats = inputs.stream()
                                                  .mapToDouble(i -> i.getMentionVelocity().doubleValue())
                                                  .summaryStatistics();
    DoubleSummaryStatistics sourceStats = inputs.stream()
                                                .mapToDouble(i -> i.getSourceCount())
                                                .summaryStatistics();
    return inputs.stream().map(i -> toScore(i, velocityStats, sourceStats)).toList();
  }

  private TechnologyScore toScore(TechnologyScoreInput input, DoubleSummaryStatistics velocityStats,
        DoubleSummaryStatistics sourceStats) {
    double velocity = input.getMentionVelocity().doubleValue();
    double categoryWeight = input.getCategoryWeight().doubleValue();
    double score = weightsProperties.getVelocity() * norm(velocity, velocityStats)
          + weightsProperties.getSourceCount() * norm(input.getSourceCount(), sourceStats)
          + weightsProperties.getCategoryWeight() * categoryWeight;

    return TechnologyScore.builder()
                          .technologyId(input.getTechnologyId())
                          .canonicalName(input.getCanonicalName())
                          .categoryCode(input.getCategoryCode())
                          .score(score)
                          .mentionVelocity(velocity)
                          .sourceCount(input.getSourceCount())
                          .categoryWeight(categoryWeight)
                          .build();
  }
}
