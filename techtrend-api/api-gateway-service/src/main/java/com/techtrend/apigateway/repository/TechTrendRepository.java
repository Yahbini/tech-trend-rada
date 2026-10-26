package com.techtrend.apigateway.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.techtrend.apigateway.config.ElasticsearchIndexProperties;
import com.techtrend.apigateway.dto.request.TopSortField;
import com.techtrend.common.document.TechTrendDocument;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

/**
 * Read-only queries on index tech-trend-aggregated for the REST API.
 */
@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendRepository {
  ElasticsearchClient elasticsearchClient;
  ElasticsearchIndexProperties esIndexProperties;

  /**
   * window_start of the newest window that has data in the category (all categories when null).
   */
  public Optional<String> findLatestWindowStart(String category) {
    try {
      SearchResponse<TechTrendDocument> response = elasticsearchClient
            .search(s ->
                  s.index(esIndexProperties.getIndex())
                   .query(categoryFilter(category))
                   .sort(o -> o.field(f -> f.field("window_start").order(SortOrder.Desc)))
                   .size(1)
                   .source(src -> src.filter(f -> f.includes("window_start"))), TechTrendDocument.class
            );
      return response.hits().hits().stream().findFirst()
                     .map(hit -> hit.source().getWindowStart());
    } catch (IOException e) {
      throw new UncheckedIOException("Find latest window_start failed: category=" + category, e);
    }
  }

  /**
   * Documents of one window ranked desc by the sort field; technology_id breaks remaining ties so ranks are stable.
   */
  public List<TechTrendDocument> findTop(String windowStart, String category, TopSortField sortBy, int limit) {
    try {
      SearchResponse<TechTrendDocument> response = elasticsearchClient.search(s ->
            s.index(esIndexProperties.getIndex())
             .query(q -> q.bool(b ->
                   b.filter(f -> f.term(t -> t.field("window_start").value(windowStart)))
                    .filter(categoryFilter(category))))
             .sort(o -> o.field(f -> f.field(sortBy.getField()).order(SortOrder.Desc)))
             .sort(o -> o.field(f -> f.field(sortBy.getTieBreakField()).order(SortOrder.Desc)))
             .sort(o -> o.field(f -> f.field("technology_id").order(SortOrder.Asc)))
             .size(limit), TechTrendDocument.class
      );
      return response.hits().hits().stream().map(Hit::source).toList();
    } catch (IOException e) {
      throw new UncheckedIOException("Find top failed: window=" + windowStart + " category=" + category, e);
    }
  }

  private Query categoryFilter(String category) {
    if (category == null) return Query.of(q -> q.matchAll(m -> m));
    return Query.of(q -> q.bool(b -> b.filter(f -> f.term(t -> t.field("category").value(category)))));
  }
}
