package com.techtrend.telegrambotservice.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Display data for a new-tech alert, already enriched from MySQL; name and category may still be null.
 */
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewTechAlertDto {
  long technologyId;
  String technologyName;
  String categoryCode;
  String source;
  String url;
}
