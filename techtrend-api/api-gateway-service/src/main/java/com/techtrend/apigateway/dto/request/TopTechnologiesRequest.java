package com.techtrend.apigateway.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Query params of GET /top; window = window_start of a 1h window, null means the latest one.
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopTechnologiesRequest {
  String category;
  Instant window;
  Integer limit = 10;
  TopSortField sortBy = TopSortField.COUNT;
}
