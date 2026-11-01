package com.techtrend.trendscoring.repository;

import com.techtrend.trendscoring.dto.TechnologyScoreInput;
import com.techtrend.trendscoring.entities.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
  /**
   * All technologies, since min–max normalization needs every row even when scoring one.
   * sourceCount = distinct sources mentioning the technology since the given instant.
   */
  @Query("""
            select new com.techtrend.trendscoring.dto.TechnologyScoreInput(
                t.id, t.canonicalName, c.code, t.mentionVelocity,
                (select count(distinct r.sourceId)
                 from TechnologyMention m join m.rawCrawlLog r
                 where m.technology = t and m.mentionedAt >= :since),
                  coalesce(c.weight, 0.50bd))
            from Technology t left join t.category c
        """)
  List<TechnologyScoreInput> findAllScoreInputs(@Param("since") Instant since);
}
