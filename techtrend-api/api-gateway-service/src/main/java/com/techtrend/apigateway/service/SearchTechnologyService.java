package com.techtrend.apigateway.service;

import com.techtrend.apigateway.dto.request.SearchTechnologiesRequest;
import com.techtrend.apigateway.dto.response.SearchTechnologyResponse;
import com.techtrend.apigateway.mapper.TechnologyMapper;
import com.techtrend.apigateway.repository.TechTrendRepository;
import com.techtrend.common.document.TechTrendDocument;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchTechnologyService {
  TechTrendRepository techTrendRepository;
  TechnologyMapper technologyMapper;

  /**
   * Technologies whose canonical_name matches q, one item per technology (its own latest window when request.window is null).
   */
  public List<SearchTechnologyResponse> search(SearchTechnologiesRequest request) {
    String windowStart = request.getWindow() != null ? request.getWindow().toString() : null;
    List<TechTrendDocument> documents = techTrendRepository.searchByName(request.getQ(), windowStart,
          request.getLimit());
    log.debug("search q={} window={} limit={} -> {}", request.getQ(), windowStart, request.getLimit(),
          documents.stream()
                   .map(d -> d.getTechnologyId() + "@" + d.getWindowStart() + ":" + d.getMentionCount())
                   .toList());
    return documents.stream().map(technologyMapper::toSearchResponse).toList();
  }
}
