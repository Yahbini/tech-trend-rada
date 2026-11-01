package com.techtrend.trendscoring.service;

import com.techtrend.trendscoring.model.TechnologyScore;
import com.techtrend.trendscoring.repository.TechnologyRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoringQueryService {
  private static final Duration SOURCE_WINDOW = Duration.ofDays(7);
  TechnologyRepository  technologyRepository;
  ScoreCalculator scoreCalculator;

  /**
   * Scores every technology over the rolling 7-day window; callers filter afterwards
   * because normalization needs the full list.
   */
  @Transactional(readOnly = true)
  public List<TechnologyScore> findAllScores() {
    Instant since = Instant.now().minus(SOURCE_WINDOW);
    return scoreCalculator.calculate(technologyRepository.findAllScoreInputs(since));
  }
}
