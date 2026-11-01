package com.techtrend.crawlingest.service;

import com.techtrend.crawlingest.entities.Category;
import com.techtrend.crawlingest.entities.RawCrawlLog;
import com.techtrend.crawlingest.entities.Technology;
import com.techtrend.crawlingest.entities.TechnologyMention;
import com.techtrend.crawlingest.repository.CategoryRepository;
import com.techtrend.crawlingest.repository.TechnologyMentionRepository;
import com.techtrend.crawlingest.repository.TechnologyRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechnologyService {
  static String OTHER_CATEGORY_CODE = "other";
  TechnologyRepository technologyRepository;
  TechnologyMentionRepository technologyMentionRepository;
  CategoryRepository categoryRepository;

  /**
   * Inserts new technologies; existing ones are managed and flushed by dirty checking on commit.
   */
  public void upsert(Technology technology) {
    Instant now = Instant.now();
    technology.setLastSeenAt(now);
    if (technology.getId() != null) {
      return;
    }
    technology.setFirstSeenAt(now);
    technologyRepository.save(technology);
  }

  public void recordMention(Technology technology, RawCrawlLog rawCrawlLog) {
    TechnologyMention technologyMention = TechnologyMention.builder()
                                                           .technology(technology)
                                                           .rawCrawlLog(rawCrawlLog)
                                                           .mentionedAt(rawCrawlLog.getCrawledAt())
                                                           .build();
    technologyMentionRepository.save(technologyMention);
  }

  public Technology findOrNew(String canonicalName) {
    return technologyRepository.findByCanonicalName(canonicalName)
                               .orElseGet(() -> Technology.builder()
                                                          .canonicalName(canonicalName)
                                                          .build());
  }

  /**
   * Only fills a missing category, so manual corrections in DB are kept.
   *
   * @param technology
   */
  public void assignCategory(Technology technology) {
    if (technology.getCategory() != null) {
      return;
    }

    Category category = categoryRepository
          .findByCanonicalName(technology.getCanonicalName())
          .orElseGet(() -> categoryRepository.findByCode(OTHER_CATEGORY_CODE)
                                             .orElseThrow(() -> new IllegalStateException(
                                                   "Category 'other' is not seeded (V4)")));
    technology.setCategory(category);
  }

  /**
   * New (unsaved, id == null) → true; existing → false so the new-tech alert fires only once.
   */
  public void assignIsNew(Technology technology) {
    technology.setIsNew(technology.getId() == null);
  }

  public void calculateMentionCountLast7d(Technology technology) {
    if (technology.getId() == null) {
      technology.setMentionCountLast7d(1);
      return;
    }
    Instant now = Instant.now();
    Long previousMentionTotalCount = technologyMentionRepository
          .countInWindow(technology.getId(), now.minus(7, ChronoUnit.DAYS), now);
    technology.setMentionCountLast7d((int) (previousMentionTotalCount + 1));
  }

  /**
   * Method used to check whether a technology was mentioned more or less frequently than the previous week, and by how much.
   */
  public void calculateMentionVelocity(Technology technology) {
    // number of mentions in the previous 7 days
    int prev7d = 0;
    if (technology.getId() != null) {
      Instant now = Instant.now();
      prev7d = technologyMentionRepository
            .countInWindow(technology.getId(), now.minus(14, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS))
            .intValue();
    }
    // Number of mentions in the last 7 days
    int last7d = technology.getMentionCountLast7d();
    technology.setMentionCountPrev7d(prev7d);
    //    velocity = (last_7d - prev_7d) / max(prev_7d, 1)
    technology.setMentionVelocity(velocityOf(last7d, prev7d));
  }

  /**
   * Daily job: recounts the 7-day stats from technology_mention, so technologies no longer mentioned decay to 0.
   * Unlike the hot path there is no "+1": no new mention is being saved here.
   * Unchanged stats are left untouched, so the row gets no UPDATE and tech-updates gets no extra CDC event.
   *
   * @return true if the row changed and will be UPDATEd on commit
   */
  public boolean recalculateSevenDayStats(Technology technology, Instant now) {
    Instant weekAgo = now.minus(7, ChronoUnit.DAYS);
    int last7d = technologyMentionRepository.countInWindow(technology.getId(), weekAgo, now).intValue();
    int prev7d = technologyMentionRepository
          .countInWindow(technology.getId(), now.minus(14, ChronoUnit.DAYS), weekAgo)
          .intValue();
    BigDecimal velocity = velocityOf(last7d, prev7d);

    // compareTo, not equals: BigDecimal 1.0 vs 1.0000 are equal in value but not in equals().
    boolean unchanged = Objects.equals(technology.getMentionCountLast7d(), last7d)
          && Objects.equals(technology.getMentionCountPrev7d(), prev7d)
          && technology.getMentionVelocity() != null
          && technology.getMentionVelocity().compareTo(velocity) == 0;
    if (unchanged) {
      return false;
    }
    technology.setMentionCountLast7d(last7d);
    technology.setMentionCountPrev7d(prev7d);
    technology.setMentionVelocity(velocity);
    return true;
  }

  // velocity = (last_7d - prev_7d) / max(prev_7d, 1); shared by hot path and daily job.
  private BigDecimal velocityOf(int last7d, int prev7d) {
    return BigDecimal.valueOf(last7d - prev7d)
                     .divide(BigDecimal.valueOf(Math.max(prev7d, 1)), 4, RoundingMode.HALF_UP);
  }

  /**
   * Re-crawl of a post already counted for this technology → skip to avoid double count and extra CDC events.
   */
  public boolean isAlreadyCounted(Technology technology, RawCrawlLog rawCrawlLog) {
    return technology.getId() != null && technologyMentionRepository.existsForPost(technology.getId(),
          rawCrawlLog.getSource().getId(), rawCrawlLog.getExternalId());
  }
}