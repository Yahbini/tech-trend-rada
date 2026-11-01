package com.techtrend.essinkconsumerservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.techtrend.common.document.TechTrendDocument;
import com.techtrend.essinkconsumerservice.config.ElasticsearchIndexProperties;
import com.techtrend.essinkconsumerservice.exception.NonRetryableIndexException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Writes one document per call; same id overwrites, so replaying a record is idempotent.
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechTrendIndexService {
  ElasticsearchClient elasticsearchClient;
  ElasticsearchIndexProperties indexProperties;

  public void index(String documentId, TechTrendDocument document) {
    try {
      IndexResponse response = elasticsearchClient.index(i -> i.index(indexProperties.getIndex())
                                                                    .id(documentId)
                                                                    .document(document));
      log.debug("Indexed tech-trend document: id={} result={}", documentId, response.result());
    } catch (IOException e) {
      // Propagate so the listener skips ack and the record is retried;
      throw new UncheckedIOException("Index tech-trend document failed: id=" + documentId, e);
    } catch (ElasticsearchException e) {
      // 4xx = request itself is wrong (e.g. strict mapping); 429 is only overload, so it stays retryable.
      if (e.status() >= 400 && e.status() < 500 && e.status() != 429) {
        throw new NonRetryableIndexException("ES rejected tech-trend document: id=" + documentId
              + " status=" + e.status() + " type=" + e.error().type() + " reason=" + e.error().reason(), e);
      }
      throw e;
    }
  }
}
