package com.techtrend.telegrambotservice.service;

import com.techtrend.telegrambotservice.entities.AlertLog;
import com.techtrend.telegrambotservice.repository.AlertLogRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlertLogService {
  AlertLogRepository alertLogRepository;

  /**
   * Reserves today's alert slot for a technology; empty when it was already alerted today (UTC).
   */
  public Optional<Long> reserve(Long technologyId, String canonicalName) {
    AlertLog alertLog = AlertLog.builder()
                                .technologyId(technologyId)
                                .canonicalName(canonicalName)
                                .sentAt(Instant.now())
                                .build();

    try {
      return Optional.of(alertLogRepository.saveAndFlush(alertLog).getId());
    } catch (DataIntegrityViolationException e) {
      return Optional.empty();
    }
  }

  @Transactional
  public void markSent(Long alertLogId, Long messageId) {
    AlertLog alertLog = alertLogRepository.findById(alertLogId)
                                          .orElseThrow(() -> new IllegalStateException(
                                                "Reserved alert_log row not found: id=" + alertLogId));
    alertLog.setTelegramMessageId(String.valueOf(messageId));
    alertLogRepository.save(alertLog);
  }


  /**
   * Frees a reservation whose send failed, so the technology can still be alerted later the same day.
   */
  public void release(Long alertLogId) {
    alertLogRepository.deleteById(alertLogId);
  }
}
