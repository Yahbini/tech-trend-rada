package com.techtrend.essinkconsumerservice.service;

import com.techtrend.common.document.TechTrendDocument;
import com.techtrend.essinkconsumerservice.dto.TechnologyLookup;
import com.techtrend.essinkconsumerservice.mapper.TechTrendDocumentMapper;
import com.techtrend.essinkconsumerservice.repository.TechnologyRepository;
import com.techtrendradar.avro.TechTrendAggregated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechTrendDocumentServiceTest {
  @Mock
  TechnologyRepository technologyRepository;
  @Mock
  CategoryCodeCache categoryCodeCache;

  TechTrendDocumentService service;

  @BeforeEach
  void setUp() {
    service = new TechTrendDocumentService(technologyRepository, categoryCodeCache, new TechTrendDocumentMapper());
  }

  @Test
  void toDocument_usesAvroValuesWithoutMySqlLookup() {
    when(categoryCodeCache.codeOf(7L)).thenReturn("messaging");

    TechTrendDocument document = service.toDocument(aggregated(47L, "ActiveMQ", 7L));

    assertThat(document.getCanonicalName()).isEqualTo("ActiveMQ");
    assertThat(document.getCategory()).isEqualTo("messaging");
    verify(technologyRepository, never()).findLookupById(anyLong());
  }

  @Test
  void toDocument_fillsMissingNameAndCategoryFromMySql() {
    when(technologyRepository.findLookupById(47L)).thenReturn(Optional.of(new TechnologyLookup("ActiveMQ", 7L)));
    when(categoryCodeCache.codeOf(7L)).thenReturn("messaging");

    TechTrendDocument document = service.toDocument(aggregated(47L, null, null));

    assertThat(document.getCanonicalName()).isEqualTo("ActiveMQ");
    assertThat(document.getCategoryId()).isEqualTo(7L);
    assertThat(document.getCategory()).isEqualTo("messaging");
  }

  @Test
  void toDocument_unknownTechnologyStillBuildsDocumentWithoutNameAndCategory() {
    when(technologyRepository.findLookupById(999301L)).thenReturn(Optional.empty());

    TechTrendDocument document = service.toDocument(aggregated(999301L, null, null));

    assertThat(document.getTechnologyId()).isEqualTo(999301L);
    assertThat(document.getCanonicalName()).isNull();
    assertThat(document.getCategoryId()).isNull();
    assertThat(document.getCategory()).isNull();
    assertThat(document.getMentionCount()).isEqualTo(1L);
  }

  private static TechTrendAggregated aggregated(long technologyId, String canonicalName, Long categoryId) {
    return TechTrendAggregated.newBuilder()
                              .setTechnologyId(technologyId)
                              .setCanonicalName(canonicalName)
                              .setCategoryId(categoryId)
                              .setWindowStart(Instant.parse("2026-10-18T00:00:00Z"))
                              .setWindowEnd(Instant.parse("2026-10-18T01:00:00Z"))
                              .setMentionCount(1L)
                              .setMentionVelocity(0.0)
                              .setSourceCounts(Map.of("devto", 1L))
                              .build();
  }
}
