package com.techtrend.telegrambotservice.service;

import com.techtrend.telegrambotservice.client.TelegramClient;
import com.techtrend.telegrambotservice.client.TelegramErrors;
import com.techtrend.telegrambotservice.dto.TechnologyLookup;
import com.techtrend.telegrambotservice.formatter.NewTechAlertFormatter;
import com.techtrend.telegrambotservice.mapper.NewTechAlertMapper;
import com.techtrend.telegrambotservice.repository.TechnologyRepository;
import com.techtrendradar.avro.NewTechDetected;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechAlertService {
  TechnologyRepository technologyRepository;
  AlertLogService alertLogService;
  NewTechAlertMapper newTechAlertMapper;
  NewTechAlertFormatter newTechAlertFormatter;
  TelegramClient telegramClient;

  /**
   * Looks up the technology, reserves today's alert slot, then sends it to Telegram.
   * Unknown or already-alerted technologies are skipped; a failed send releases the reservation and is only logged.
   */
  public void handle(NewTechDetected newTechDetected) {
    long technologyId = newTechDetected.getTechnologyId();

    Optional<TechnologyLookup> lookup = technologyRepository.findLookupById(technologyId);

    if (lookup.isEmpty()) {
      log.warn("Technology not found, skip alert: technologyId={}", technologyId);
      return;
    }

    Optional<Long> alertLogId = alertLogService.reserve(technologyId, lookup.get().getCanonicalName());
    if (alertLogId.isEmpty()) {
      log.info("Already alerted today, skip: technologyId={}", technologyId);
      return;
    }

    String text = newTechAlertFormatter.format(newTechAlertMapper.toDto(newTechDetected, lookup.get()));
    Long messageId;
    try {
      messageId = telegramClient.sendMessage(text);
    } catch (RuntimeException e) {
      alertLogService.release(alertLogId.get());
      log.error("Send new-tech alert failed, reservation released: technologyId={} alertLogId={} cause={}",
            technologyId, alertLogId.get(), TelegramErrors.describe(e));
      return;
    }
    alertLogService.markSent(alertLogId.get(), messageId);
    log.info("New-tech alert sent: technologyId={} alertLogId={} messageId={}",
          technologyId, alertLogId.get(), messageId);
  }
}
