package com.techtrend.telegrambotservice.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Binds app.telegram.*; no toString on purpose, the bot token must never reach logs.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "app.telegram")
public class TelegramProperties {
  String baseUrl;
  String botToken;
  String chatId;
  Duration connectTimeout;
  Duration readTimeout;
  Retry retry = new Retry();

  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Retry {
    long maxRetries;
    Duration delay;
    double multiplier;
    Duration timeout;
    Duration maxRetryAfter;
  }
}
