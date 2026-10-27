package com.techtrend.apigateway.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Error body of every endpoint; keeps Spring's default fields and adds a human-readable message.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorResponse {
  Instant timestamp;
  Integer status;
  String error;
  String message;
  String path;
}
