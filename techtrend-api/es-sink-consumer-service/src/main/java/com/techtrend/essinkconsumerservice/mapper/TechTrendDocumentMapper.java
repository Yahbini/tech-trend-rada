package com.techtrend.essinkconsumerservice.mapper;

import com.techtrend.common.document.TechTrendDocument;
import com.techtrendradar.avro.TechTrendAggregated;
import org.springframework.stereotype.Component;

@Component
public class TechTrendDocumentMapper {
  // Name/category are resolved by the caller (Avro value or MySQL fallback), so this stays a pure function.
  public TechTrendDocument toTechTrendDocument(TechTrendAggregated aggregated, String canonicalName, Long categoryId,
        String categoryCode) {
    return TechTrendDocument.builder()
                            .technologyId(aggregated.getTechnologyId())
                            .canonicalName(canonicalName)
                            .categoryId(categoryId)
                            .category(categoryCode)
                            .windowStart(aggregated.getWindowStart().toString())
                            .windowEnd(aggregated.getWindowEnd().toString())
                            .mentionCount(aggregated.getMentionCount())
                            .mentionVelocity(aggregated.getMentionVelocity())
                            .sourceCounts(aggregated.getSourceCounts())
                            .build();
  }

  // ES _id = natural key (technology × window) so a replayed record overwrites instead of duplicating.
  public String documentIdOf(TechTrendAggregated aggregated) {
    return aggregated.getTechnologyId() + "_" + aggregated.getWindowStart() ;
  }
}
