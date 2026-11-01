package com.techtrend.apigateway.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Query params of GET /search; q matches canonical_name (partial, case-insensitive).
 * window = window_start of a 1h window; null means each technology's own latest window (not index-wide like /top).
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchTechnologiesRequest {
  @NotBlank(message = "q must not be blank")
  String q;
  Integer limit = 10;
  Instant window;
}
