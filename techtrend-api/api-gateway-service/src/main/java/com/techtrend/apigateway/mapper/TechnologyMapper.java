package com.techtrend.apigateway.mapper;

import com.techtrend.apigateway.dto.response.SearchTechnologyResponse;
import com.techtrend.apigateway.dto.response.TopTechnologyResponse;
import com.techtrend.common.document.TechTrendDocument;
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
}
