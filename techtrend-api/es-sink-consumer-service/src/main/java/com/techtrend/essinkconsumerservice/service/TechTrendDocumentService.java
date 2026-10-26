package com.techtrend.essinkconsumerservice.service;

import com.techtrend.common.document.TechTrendDocument;
import com.techtrend.essinkconsumerservice.dto.TechnologyLookup;
import com.techtrend.essinkconsumerservice.mapper.TechTrendDocumentMapper;
import com.techtrend.essinkconsumerservice.repository.TechnologyRepository;
import com.techtrendradar.avro.TechTrendAggregated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Builds the ES document for one aggregated window; name/category missing in Avro (CDC not arrived yet) are filled from MySQL.
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendDocumentService {
  TechnologyRepository technologyRepository;
  CategoryCodeCache categoryCodeCache;
  TechTrendDocumentMapper techTrendDocumentMapper;

  public TechTrendDocument toDocument(TechTrendAggregated aggregated) {
    String canonicalName = aggregated.getCanonicalName() != null ? aggregated.getCanonicalName().toString() : null;
    Long categoryId = aggregated.getCategoryId();

    if (canonicalName == null || categoryId == null) {
      Optional<TechnologyLookup> lookup = technologyRepository.findLookupById(aggregated.getTechnologyId());
      if (lookup.isPresent()) {
        canonicalName = canonicalName != null ? canonicalName : lookup.get().getCanonicalName();
        categoryId = categoryId != null ? categoryId : lookup.get().getCategoryId();
      } else {
        // Still indexed: mention stats are valid even without a name; /search just won't find it.
        log.warn("Technology not found in MySQL, indexing without name/category: technologyId={}", aggregated.getTechnologyId());
      }
    }

    return techTrendDocumentMapper.toTechTrendDocument(aggregated, canonicalName, categoryId, categoryCodeCache.codeOf(categoryId));
  }
}
