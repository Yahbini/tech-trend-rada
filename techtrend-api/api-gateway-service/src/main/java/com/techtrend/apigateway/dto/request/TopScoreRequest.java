package com.techtrend.apigateway.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Query params of GET /top (7-day score); null category means all categories.
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopScoreRequest {
  @Pattern(regexp = CategoryCodes.PATTERN, message = CategoryCodes.MESSAGE)
  String category;

  @Min(value = 1, message = "limit must be between 1 and 100")
  @Max(value = 100, message = "limit must be between 1 and 100")
  Integer limit = 10;
}
