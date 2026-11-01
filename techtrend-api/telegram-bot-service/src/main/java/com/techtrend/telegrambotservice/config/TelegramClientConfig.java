package com.techtrend.telegrambotservice.config;

import com.techtrend.telegrambotservice.client.TelegramErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryState;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class TelegramClientConfig {
  // Network/timeout, 5xx and 429 may succeed later; other 4xx (bad HTML, wrong token, blocked) never will.
  static boolean isTransient(Throwable e) {
    return e instanceof ResourceAccessException || e instanceof HttpServerErrorException || e instanceof HttpClientErrorException.TooManyRequests;
  }

  /**
   * RestClient rooted at https://api.telegram.org/bot<token>, so callers only pass the method path.
   */
  @Bean
  public RestClient telegramRestClient(RestClient.Builder builder, TelegramProperties properties) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(properties.getConnectTimeout());
    requestFactory.setReadTimeout(properties.getReadTimeout());

    return builder
          .baseUrl(properties.getBaseUrl() + "/bot" + properties.getBotToken())
          .requestFactory(requestFactory)
          .build();
  }

  /**
   * Retries only transient Telegram failures, with exponential backoff and an overall time cap.
   */
  @Bean
  public RetryTemplate telegramRetryTemplate(TelegramProperties telegramProperties) {
    TelegramProperties.Retry retry = telegramProperties.getRetry();
    RetryPolicy retryPolicy = RetryPolicy.builder()
                                         .maxRetries(retry.getMaxRetries())
                                         .delay(retry.getDelay())
                                         .timeout(retry.getTimeout())
                                         .multiplier(retry.getMultiplier())
                                         .predicate(TelegramClientConfig::isTransient)
                                         .build();

    RetryTemplate retryTemplate = new RetryTemplate(retryPolicy);
    retryTemplate.setRetryListener(new RetryListener() {
      @Override
      public void beforeRetry(RetryPolicy policy, Retryable<?> retryable, RetryState state) {
        log.warn("Telegram call failed, retry {}/{}: {}",
              state.getRetryCount(), retry.getMaxRetries(), TelegramErrors.describe(state.getLastException()));
      }
    });
    return retryTemplate;
  }
}
