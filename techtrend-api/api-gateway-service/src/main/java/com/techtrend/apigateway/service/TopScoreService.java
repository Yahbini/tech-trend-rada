package com.techtrend.apigateway.service;

import com.techtrend.apigateway.client.TrendScoringClient;
import com.techtrend.apigateway.dto.request.TopScoreRequest;
import com.techtrend.apigateway.dto.response.TopScoreResponse;
import com.techtrend.apigateway.mapper.TechnologyMapper;
import com.techtrendradar.grpc.scoring.ScoreResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopScoreService {
  TrendScoringClient trendScoringClient;
  TechnologyMapper technologyMapper;

  /**
   * Top technologies by 7-day score from trend-scoring-service; rank follows the server's score order.
   */
  public List<TopScoreResponse> findTop(TopScoreRequest request) {
    List<ScoreResponse> scores = trendScoringClient.getTopScores(request.getCategory(), request.getLimit());
    log.debug("top score category={} limit={} -> {} items", request.getCategory(), request.getLimit(), scores.size());
    return IntStream.range(0, scores.size())
                    .mapToObj(i -> technologyMapper.toTopScoreResponse(scores.get(i), i + 1))
                    .toList();
  }
}
