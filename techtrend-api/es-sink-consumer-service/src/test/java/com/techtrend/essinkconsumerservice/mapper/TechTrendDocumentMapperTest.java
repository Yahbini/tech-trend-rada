package com.techtrend.essinkconsumerservice.mapper;

import co.elastic.clients.json.JsonpUtils;
import co.elastic.clients.json.jackson.Jackson3JsonpMapper;
import com.techtrend.essinkconsumerservice.document.TechTrendDocument;
import com.techtrendradar.avro.TechTrendAggregated;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TechTrendDocumentMapperTest {
  private final TechTrendDocumentMapper mapper = new TechTrendDocumentMapper();

  @Test
  void toTechTrendDocument_serializesAllMappingFieldsInSnakeCase() {
    TechTrendDocument document = mapper.toTechTrendDocument(aggregated(), "Kafka", 7L, "messaging");

    // Same JsonpMapper family as the auto-configured client, so this is the JSON ES receives.
    JsonNode json = JsonMapper.shared().readTree(JsonpUtils.toJsonString(document, new Jackson3JsonpMapper()));

    assertThat(json.propertyNames()).containsExactlyInAnyOrderElementsOf(List.of(
          "technology_id", "canonical_name", "category_id", "category", "window_start", "window_end",
          "mention_count", "mention_velocity", "source_counts"));
    assertThat(json.get("technology_id").asLong()).isEqualTo(13L);
    assertThat(json.get("canonical_name").asString()).isEqualTo("Kafka");
    assertThat(json.get("category_id").asLong()).isEqualTo(7L);
    assertThat(json.get("category").asString()).isEqualTo("messaging");
    assertThat(json.get("window_start").asString()).isEqualTo("2026-10-10T08:00:00Z");
    assertThat(json.get("window_end").asString()).isEqualTo("2026-10-10T09:00:00Z");
    assertThat(json.get("mention_count").asLong()).isEqualTo(3L);
    assertThat(json.get("mention_velocity").asDouble()).isEqualTo(0.5);
    assertThat(json.get("source_counts").get("hackerNews").asLong()).isEqualTo(2L);
    assertThat(json.get("source_counts").get("github").asLong()).isEqualTo(1L);
  }

  @Test
  void toTechTrendDocument_omitsUnresolvedNameAndCategory() {
    TechTrendDocument document = mapper.toTechTrendDocument(aggregated(), null, null, null);

    JsonNode json = JsonMapper.shared().readTree(JsonpUtils.toJsonString(document, new Jackson3JsonpMapper()));

    assertThat(json.has("canonical_name")).isFalse();
    assertThat(json.has("category_id")).isFalse();
    assertThat(json.has("category")).isFalse();
    assertThat(json.get("technology_id").asLong()).isEqualTo(13L);
  }

  @Test
  void documentIdOf_isTechnologyIdAndWindowStart() {
    assertThat(mapper.documentIdOf(aggregated())).isEqualTo("13_2026-10-10T08:00:00Z");
  }

  private static TechTrendAggregated aggregated() {
    return TechTrendAggregated.newBuilder()
                              .setTechnologyId(13L)
                              .setCanonicalName("Kafka")
                              .setCategoryId(7L)
                              .setWindowStart(Instant.parse("2026-10-10T08:00:00Z"))
                              .setWindowEnd(Instant.parse("2026-10-10T09:00:00Z"))
                              .setMentionCount(3L)
                              .setMentionVelocity(0.5)
                              .setSourceCounts(Map.of("hackerNews", 2L, "github", 1L))
                              .build();
  }
}
