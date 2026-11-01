package com.techtrend.telegrambotservice.client;

import com.techtrend.telegrambotservice.config.TelegramProperties;
import com.techtrend.telegrambotservice.dto.request.SendMessageRequest;
import com.techtrend.telegrambotservice.dto.response.TelegramMessage;
import com.techtrend.telegrambotservice.dto.response.TelegramResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Calls Telegram Bot API for the single configured chat; 4xx/5xx surface as RestClient exceptions.
 */
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramClient {
  // Must match the markup NewTechAlertFormatter produces.
  private static final String PARSE_MODE = "HTML";

  private static final ParameterizedTypeReference<TelegramResponse<TelegramMessage>> MESSAGE_RESPONSE = new ParameterizedTypeReference<>() {
  };

  RestClient telegramRestClient;
  TelegramProperties telegramProperties;
  RetryTemplate telegramRetryTemplate;

  /**
   * Sends text to the configured chat and returns Telegram's message_id.
   */
  public Long sendMessage(String text) {
    SendMessageRequest request = SendMessageRequest.builder()
                                                   .chatId(telegramProperties.getChatId())
                                                   .text(text)
                                                   .parseMode(PARSE_MODE)
                                                   .build();


    return telegramRetryTemplate.invoke(() -> sendOnce(request));
  }

  private Long sendOnce(SendMessageRequest request) {
    TelegramResponse<TelegramMessage> response;

    try {
      response = telegramRestClient.post()
                                   .uri("/sendMessage")
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .body(request)
                                   .retrieve()
                                   .body(MESSAGE_RESPONSE);
    } catch (HttpClientErrorException.TooManyRequests e) {
      waitRetryAfter(e);
      throw e;
    }

    // A 2xx without message_id is unexpected; failing beats storing null, which alert_log reads as "reserved, not sent"
    if (response == null || !response.isOk() || response.getResult() == null
          || response.getResult().getMessageId() == null) {
      throw new IllegalStateException("Telegram sendMessage returned no message_id");
    }

    return response.getResult().getMessageId();
  }

  // Honors Telegram's retry_after, then the 429 is rethrown so RetryTemplate still counts the attempt.
  private void waitRetryAfter(HttpClientErrorException.TooManyRequests e) {
    Duration retryAfter = readRetryAfter(e);
    if (retryAfter == null) {
      return;
    }

    if (retryAfter.compareTo(telegramProperties.getRetry().getMaxRetryAfter()) > 0) {
      throw new IllegalStateException("Telegram retry_after " + retryAfter.toSeconds() + "s exceeds cap", e);
    }

    try {
      Thread.sleep(retryAfter.toMillis());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for Telegram retry_after", ie);
    }
  }

  private static Duration readRetryAfter(HttpClientErrorException e) {
    try {
      TelegramResponse<?> body = e.getResponseBodyAs(TelegramResponse.class);
      if (body == null || body.getParameters() == null || body.getParameters().getRetryAfter() == null) {
        return null;
      }
      return Duration.ofSeconds(body.getParameters().getRetryAfter());
    } catch (RuntimeException parseError) {
      return null;
    }
  }
}
