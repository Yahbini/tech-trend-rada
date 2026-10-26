package com.techtrend.apigateway.service;

import com.techtrend.apigateway.dto.request.TopTechnologiesRequest;
import com.techtrend.apigateway.dto.response.TopTechnologyResponse;
import com.techtrend.apigateway.mapper.TopTechnologyMapper;
import com.techtrend.apigateway.repository.TechTrendRepository;
import com.techtrend.common.document.TechTrendDocument;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopTechnologyService {
  TechTrendRepository techTrendRepository;
  private final TopTechnologyMapper topTechnologyMapper;

  /**
   * Top technologies of one 1h window, ranked by ES at query time (latest window when request.window is null).
   */
  public List<TopTechnologyResponse> findTop(TopTechnologiesRequest request) {
    Optional<String> windowStart = request.getWindow() != null
          ? Optional.of(request.getWindow().toString()) :
          techTrendRepository.findLatestWindowStart(request.getCategory());
    if (windowStart.isEmpty()) return List.of();

    List<TechTrendDocument> documents = techTrendRepository.findTop(windowStart.get(), request.getCategory(),
          request.getSortBy(), request.getLimit());
    log.debug("top window={} category={} sort={} limit={} -> {}",
          windowStart.get(), request.getCategory(), request.getSortBy(), request.getLimit(),
          documents.stream()
                   .map(d -> d.getTechnologyId() + ":" + d.getMentionCount() + "/" + d.getMentionVelocity())
                   .toList());

    // Mapping documents to response
    return IntStream.range(0, documents.size())
                    .mapToObj(i -> topTechnologyMapper.toResponse(documents.get(i), i + 1))
                    .toList();
  }
}
