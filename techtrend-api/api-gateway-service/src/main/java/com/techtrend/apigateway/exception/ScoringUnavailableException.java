package com.techtrend.apigateway.exception;

public class ScoringUnavailableException extends RuntimeException {
  public ScoringUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
