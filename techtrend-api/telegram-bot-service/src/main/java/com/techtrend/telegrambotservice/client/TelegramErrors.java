package com.techtrend.telegrambotservice.client;
import com.techtrend.telegrambotservice.dto.response.TelegramResponse;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Log-safe summaries of Telegram call failures; never includes the request URL, which carries the bot token.
 */
public final class TelegramErrors {
  private TelegramErrors() {
  }

  public static String describe(Throwable e) {
    if (e instanceof HttpStatusCodeException statusError) {
      return "HTTP " + statusError.getStatusCode().value() + " " + telegramDescription(statusError);
    }
    // ResourceAccessException's message embeds the full URL (and token), so only the cause type is kept.
    if (e instanceof ResourceAccessException) {
      Throwable cause = e.getCause();
      return "I/O error: " + (cause != null ? cause.getClass().getSimpleName() : "unknown");
    }
    return e.getClass().getSimpleName() + ": " + e.getMessage();
  }

  private static String telegramDescription(HttpStatusCodeException e) {
    try {
      TelegramResponse<?> body = e.getResponseBodyAs(TelegramResponse.class);
      return body != null && body.getDescription() != null ? body.getDescription() : e.getStatusText();
    } catch (RuntimeException parseError) {
      return e.getStatusText();
    }
  }
}
