package com.techtrend.streamprocessing.service;

import com.techtrend.streamprocessing.model.MentionTechMatch;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechDetectionService {
  Duration newTechWindow;

  public NewTechDetectionService(@Value("${app.detection.new-tech-window}") Duration newTechWindow) {
    this.newTechWindow = newTechWindow;
  }

  /**
   * New = technology row not in the KTable yet (its CDC insert is still in flight), or is_new with the mention
   * crawled within the window of first_seen_at, so a later mention that beats its CDC update does not re-alert.
   */
  public boolean isNewTechnology(MentionTechMatch match) {
    if (!match.isMatched()) {
      return true;
    }
    if (!match.isNewFlag()) {
      return false;
    }

    Instant firstSeenAt = match.getFirstSeenAt();
    return firstSeenAt == null || match.getTechMention().getCrawledAt().isBefore(firstSeenAt.plus(newTechWindow));
  }
}
