package com.techtrend.apigateway.client;

import com.techtrendradar.grpc.scoring.ScoreResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs against a local trend-scoring-service on :9090 (start it first).
 */
@SpringBootTest
class TrendScoringClientIT {
  @Autowired
  TrendScoringClient trendScoringClient;

  @Test
  void getTopScoresReturnsLimitedListSortedByScoreDesc() {
    List<ScoreResponse> scores = trendScoringClient.getTopScores(null, 3);

    assertEquals(3, scores.size());
    for (int i = 1; i < scores.size(); i++) {
      assertTrue(scores.get(i - 1).getScore() >= scores.get(i).getScore());
    }
  }

  @Test
  void getTopScoresFiltersByCategory() {
    List<ScoreResponse> scores = trendScoringClient.getTopScores("framework", null);

    assertTrue(scores.size() <= 10);
    assertTrue(scores.stream().allMatch(s -> s.getCategory().equals("framework")));
  }
}
