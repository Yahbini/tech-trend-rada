package com.techtrend.apigateway.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Ranking criterion of GET /top; each value carries its ES field and a tie-break field.
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TopSortField {
  COUNT("mention_count", "mention_velocity"),
  VELOCITY("mention_velocity", "mention_count");

  String field;
  String tieBreakField;
}
