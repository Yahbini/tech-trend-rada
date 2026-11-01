package com.techtrend.trendscoring.repository;

import com.techtrend.trendscoring.dto.TechnologyScoreInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runs against the local MySQL (docker ttr-*), compares the JPQL result with plain SQL.
 */
@SpringBootTest
class TechnologyRepositoryIT {
  @Autowired
  TechnologyRepository technologyRepository;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Test
  void findAllScoreInputsReturnsEveryTechnology() {
    Instant since = Instant.now().minus(7, ChronoUnit.DAYS);
    List<TechnologyScoreInput> inputs = technologyRepository.findAllScoreInputs(since);

    Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM technology", Integer.class);
    Integer withoutCategory = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM technology WHERE category_id IS NULL", Integer.class);

    assertEquals(total, inputs.size());
    assertEquals(withoutCategory, (int) inputs.stream().filter(i -> i.getCategoryCode() == null).count());
    assertEquals(0, inputs.stream().filter(i -> Objects.isNull(i.getMentionVelocity())).count());

    Map<Long, Long> expectedSources = jdbcTemplate.query("""
        SELECT m.technology_id, COUNT(DISTINCT r.source_id) AS cnt
        FROM technology_mention m JOIN raw_crawl_log r ON r.id = m.raw_crawl_log_id
        WHERE m.mentioned_at >= ?
        GROUP BY m.technology_id
        """, rs -> {
      Map<Long, Long> map = new java.util.HashMap<>();
      while (rs.next()) {
        map.put(rs.getLong(1), rs.getLong(2));
      }
      return map;
    }, Timestamp.from(since));
    Map<Long, Long> actualSources = inputs.stream()
        .collect(Collectors.toMap(TechnologyScoreInput::getTechnologyId, TechnologyScoreInput::getSourceCount));
    inputs.forEach(i -> assertEquals(expectedSources.getOrDefault(i.getTechnologyId(), 0L), i.getSourceCount(),
        "sourceCount of technology " + i.getTechnologyId()));

    Map<Long, java.math.BigDecimal> expectedWeights = jdbcTemplate.query("""
        SELECT t.id, COALESCE(c.weight, 0.50) FROM technology t LEFT JOIN category c ON c.id = t.category_id
        """, rs -> {
      Map<Long, java.math.BigDecimal> map = new java.util.HashMap<>();
      while (rs.next()) {
        map.put(rs.getLong(1), rs.getBigDecimal(2));
      }
      return map;
    });
    inputs.forEach(i -> assertEquals(0, expectedWeights.get(i.getTechnologyId()).compareTo(i.getCategoryWeight()),
        "categoryWeight of technology " + i.getTechnologyId()));

    inputs.stream().filter(i -> i.getSourceCount() > 0).limit(5)
        .forEach(i -> System.out.printf("SAMPLE id=%d name=%s velocity=%s sources=%d weight=%s%n",
            i.getTechnologyId(), i.getCanonicalName(), i.getMentionVelocity(), i.getSourceCount(), i.getCategoryWeight()));
    System.out.printf("SUMMARY total=%d withoutCategory=%d withSources=%d maxSources=%d%n", total, withoutCategory,
        expectedSources.size(), actualSources.values().stream().mapToLong(Long::longValue).max().orElse(0));
  }
}
