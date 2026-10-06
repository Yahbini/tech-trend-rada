package com.techtrend.streamprocessing.model;

import com.techtrendradar.avro.TechMention;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.avro.generic.GenericRecord;

import java.time.Instant;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MentionTechMatch {
  TechMention techMention;
  GenericRecord technology;

  public boolean isMatched() {
    return technology != null;
  }

  public boolean isNewFlag() {
    return isMatched() && Integer.valueOf(1).equals(technology.get("is_new"));
  }

  public Instant getFirstSeenAt() {
    Long epochMillis = isMatched() ? (Long) technology.get("first_seen_at") : null;
    return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
  }

  public CharSequence getCanonicalName() {
    return isMatched() ? (CharSequence) technology.get("canonical_name") : null;
  }

  public Long getCategoryId() {
    return  isMatched() ? (Long) technology.get("category_id") : null;
  }
}
