package com.techtrend.essinkconsumerservice.exception;

/**
 * ES rejected the document itself (4xx except 429); retrying the same record can never succeed.
 */
public class NonRetryableIndexException extends RuntimeException{
  public NonRetryableIndexException(String message, Throwable cause) {
    super(message, cause);
  }
}
