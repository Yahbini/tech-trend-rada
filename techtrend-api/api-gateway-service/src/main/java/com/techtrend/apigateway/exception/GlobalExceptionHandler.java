package com.techtrend.apigateway.exception;

import com.techtrend.apigateway.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
  /**
   * 400 for @Valid failures and query param type mismatches (e.g. window=abc) of every endpoint.
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> bindExceptionHandler(BindException ex, HttpServletRequest request) {
    String message = ex.getFieldErrors().stream()
                       .map(this::toMessage)
                       .collect(Collectors.joining("; "));
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status)
                         .body(ErrorResponse.builder()
                                            .timestamp(Instant.now())
                                            .status(status.value())
                                            // HttpStatus = BAD_REQUEST -> getReasonPhrase = "Bad Request"
                                            .error(status.getReasonPhrase())
                                            .message(message)
                                            .path(request.getRequestURI())
                                            .build());
  }

  /**
   * Spring's default type-mismatch text exposes Java class names, so replace it; validation messages are kept as-is.
   * window=abc   -> "window has invalid value 'abc'"           (binding failure)
   * category=abc -> "category must be one of: ..."             (validation failure)
   */
  private String toMessage(FieldError error) {
    if (error.isBindingFailure()) return error.getField() + " has invalid value '" + error.getRejectedValue() + "'";
    return error.getDefaultMessage();
  }
}
