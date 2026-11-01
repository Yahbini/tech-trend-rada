package com.techtrend.crawlingest.job;

import com.techtrend.crawlingest.repository.TechnologyRepository;
import com.techtrend.crawlingest.service.TechnologyService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TechnologyStatsJob {
  TechnologyRepository technologyRepository;
  TechnologyService technologyService;
  TransactionTemplate transactionTemplate;

  /**
   * Recounts last_7d / prev_7d / velocity of every technology once a day, so ones no longer mentioned decay to 0.
   * <p>
   * Each row gets its own short locked transaction: a failure skips only that row and webhooks are never overwritten.
   * - Per technology: re-read with FOR UPDATE, recalculate; unchanged row → no UPDATE, no CDC event
   * - Failed row: WARN with technology_id, continue
   * - End: one INFO summary (total, changed, failed, durati
   */
  @Scheduled(cron = "${app.stats-job.cron:0 5 0 * * *}", zone = "${app.stats-job.zone:Asia/Ho_Chi_Minh}")
  public void recalculateSevenDayStats() {
    long startedAt = System.currentTimeMillis();
    Instant now = Instant.now();
    List<Long> ids = technologyRepository.findAllIds();
    int changed = 0;
    int failed = 0;

    for (Long id : ids) {
      try {
        Boolean rowChanged = transactionTemplate.execute(status -> technologyRepository.findWithLockById(id)
                                                .map(technology -> technologyService.recalculateSevenDayStats(
                                                      technology, now))
                                                .orElse(false));
        if (Boolean.TRUE.equals(rowChanged)) {
          changed++;
        }
      } catch (RuntimeException e) {
        failed++;
        log.warn("Recalculate 7-day stats failed, technology_id={}", id, e);
      }
    }
    log.info("Recalculated 7-day stats: total={}, changed={}, failed={}, took={}ms",
          ids.size(), changed, failed, System.currentTimeMillis() - startedAt);
  }
}
