package com.techtrend.trendscoring.service;

import com.techtrend.trendscoring.config.ScoringWeightsProperties;
import com.techtrend.trendscoring.dto.TechnologyScoreInput;
import com.techtrend.trendscoring.model.TechnologyScore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoreCalculatorTest {
  private static final double DELTA = 1e-9;

  private final ScoreCalculator calculator = new ScoreCalculator(new ScoringWeightsProperties());

  @Test
  void normalizesOverTheWholeListAndAppliesWeights() {
    List<TechnologyScore> scores = calculator.calculate(List.of(
        input(1L, "3.0", 3L, "1.00"),
        input(2L, "1.0", 2L, "0.70"),
        input(3L, "-1.0", 1L, "0.50")));

    assertEquals(1.0, scores.get(0).getScore(), DELTA);  // top of both ranges
    assertEquals(0.54, scores.get(1).getScore(), DELTA); // 0.5·0.5 + 0.3·0.5 + 0.2·0.70
    assertEquals(0.10, scores.get(2).getScore(), DELTA); // negative velocity is the min → norm 0
  }

  @Test
  void sameValuesEverywhereLeaveOnlyCategoryWeight() {
    List<TechnologyScore> scores = calculator.calculate(List.of(
        input(1L, "2.0", 1L, "0.90"),
        input(2L, "2.0", 1L, "0.60")));

    assertEquals(0.18, scores.get(0).getScore(), DELTA);
    assertEquals(0.12, scores.get(1).getScore(), DELTA);
  }

  @Test
  void emptyListGivesEmptyResult() {
    assertTrue(calculator.calculate(List.of()).isEmpty());
  }

  private static TechnologyScoreInput input(Long id, String velocity, Long sources, String weight) {
    return new TechnologyScoreInput(id, "tech-" + id, "language", new BigDecimal(velocity), sources,
        new BigDecimal(weight));
  }
}
