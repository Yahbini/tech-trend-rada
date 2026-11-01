package com.techtrend.apigateway.client;

import com.techtrend.apigateway.exception.ScoringUnavailableException;
import com.techtrendradar.grpc.scoring.ScoreResponse;
import com.techtrendradar.grpc.scoring.TopScoresRequest;
import com.techtrendradar.grpc.scoring.TrendScoringServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrendScoringClient {
  private static final long DEADLINE_SECONDS = 2;
  TrendScoringServiceGrpc.TrendScoringServiceBlockingStub trendScoringStub;

  /**
   * Top scores from trend-scoring-service; null category = all, null limit = server default (10).
   */
  /**
   * Top scores from trend-scoring-service; null category = all, null limit = server default (10).
   */
  public List<ScoreResponse> getTopScores(String category, Integer limit) {
    TopScoresRequest request = TopScoresRequest.newBuilder()
                                               .setCategory(category == null ? "" : category)
                                               .setLimit(limit == null ? 0 : limit)
                                               .build();
    try {
      // Deadline is an absolute instant, so it must be set per call, not once on the bean.
      return trendScoringStub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                             .getTopScores(request)
                             .getScoresList();
    } catch (StatusRuntimeException e) {
      throw new ScoringUnavailableException("trend-scoring call failed: " + e.getStatus().getCode(), e);
    }
  }
}
