package com.techtrend.apigateway.mapper;

import com.techtrend.apigateway.dto.response.SearchTechnologyResponse;
import com.techtrend.apigateway.dto.response.TopScoreResponse;
import com.techtrend.apigateway.dto.response.TopTechnologyResponse;
import com.techtrend.common.document.TechTrendDocument;
import com.techtrendradar.grpc.scoring.ScoreResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TechnologyMapper {
  // Rank comes from the caller: a document alone does not know its position in the ES-sorted list.
  public TopTechnologyResponse toTopResponse(TechTrendDocument document, int rank) {
    return TopTechnologyResponse.builder()
                                .technologyId(document.getTechnologyId())
                                .canonicalName(document.getCanonicalName())
                                .category(document.getCategory())
                                .windowStart(Instant.parse(document.getWindowStart()))
                                .mentionCount(document.getMentionCount())
                                .mentionVelocity(document.getMentionVelocity())
                                .rank(rank)
                                .build();
  }

  public SearchTechnologyResponse toSearchResponse(TechTrendDocument document) {
    return SearchTechnologyResponse.builder()
                                   .technologyId(document.getTechnologyId())
                                   .canonicalName(document.getCanonicalName())
                                   .category(document.getCategory())
                                   .windowStart(Instant.parse(document.getWindowStart()))
                                   .mentionCount(document.getMentionCount())
                                   .mentionVelocity(document.getMentionVelocity())
                                   .build();
  }

  // Proto has no null: "" category means none. Rounding is display-only, the server already sorted.
  public TopScoreResponse toTopScoreResponse(ScoreResponse score, int rank) {
    return TopScoreResponse.builder()
                           .technologyId(score.getTechnologyId())
                           .canonicalName(score.getCanonicalName())
                           .category(score.getCategory().isEmpty() ? null : score.getCategory())
                           .score(round3(score.getScore()))
                           .mentionVelocity(round3(score.getMentionVelocity()))
                           .sourceCount(score.getSourceCount())
                           .categoryWeight(score.getCategoryWeight())
                           .rank(rank)
                           .build();
  }

  private static double round3(double value) {
    return Math.round(value * 1000) / 1000.0;
  }
}
