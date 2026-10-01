package com.techtrend.crawlingest.repository;

import com.techtrend.crawlingest.entities.TechnologyMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TechnologyMentionRepository extends JpaRepository<TechnologyMention, Long> {
  /**
   * Count how many times a technology was mentioned since the given time
   * <p>
   * Example:
   * Tech   MentionAt
   * Java   1/9
   * Java   3/9
   * C#     4/9
   * Java   5/9
   * <p>
   * Find Java from 3/9 -> from mentionAt 3/9 -> Result: 2
   */
  @Query("""
        select count(m) from TechnologyMention m
        where m.technology.id = :technologyId and m.mentionedAt >= :from and m.mentionedAt <:to
        """)
  Long countInWindow(@Param("technologyId") Long technologyId, @Param("from") Instant from, @Param("to") Instant to);

  @Query("""
        select case when count(m) > 0 then true else false end
        from TechnologyMention m
        where m.technology.id = :technologyId
        and m.rawCrawlLog.source.id = :sourceId
        and m.rawCrawlLog.externalId = :externalId
        """)
  boolean existsForPost(@Param("technologyId") Long technologyId,
        @Param("sourceId") Long sourceId,
        @Param("externalId") String externalId);
}
