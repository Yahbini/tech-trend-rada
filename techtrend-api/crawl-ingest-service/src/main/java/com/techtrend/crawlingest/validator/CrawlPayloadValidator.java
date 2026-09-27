package com.techtrend.crawlingest.validator;

import com.techtrend.crawlingest.dto.request.CrawlWebhookRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates crawl payloads sent from n8n to the webhook before they are
 * serialized to Avro and published to Kafka.
 */
@Component
public class CrawlPayloadValidator {
  /**
   * Checks that every required field is present and not blank.
   *
   * @param request the incoming webhook payload
   * @return one error per missing/blank required field; empty list means the payload passed
   */
  public List<String> validateRequiredFields(CrawlWebhookRequest request) {
    List<String> errors = new ArrayList<>();
    requirePresent(request.getSource(), "source", errors);
    requirePresent(request.getExternalId(), "external_id", errors);
    requirePresent(request.getRawText(), "raw_text", errors);
    requirePresent(request.getUrl(), "url", errors);
    requirePresent(request.getCrawledAt(), "crawled_at", errors);
    return errors;
  }

  /**
   * Checks the format of fields that have a specific shape:
   * crawled_at must be an ISO-8601 UTC instant, url must be a valid http(s) URL.
   * Fields that are missing are skipped here (they are reported by validateRequiredFields).
   *
   * @param request the incoming webhook payload
   * @return one error per malformed field; empty list means all formats are valid
   */
  public List<String> validateFormat(CrawlWebhookRequest request) {
    List<String> errors = new ArrayList<>();

    if (isPresent(request.getCrawledAt()) && !isIsoInstant(request.getCrawledAt())) {
      errors.add("crawled_at must be ISO-8601 UTC, e.g. 2026-09-27T05:00:00Z");
    }

    if (isPresent(request.getUrl()) && !isHttpUrl(request.getUrl())) {
      errors.add("url is not a valid http(s) URL");
    }

    return errors;
  }

  /**
   * Adds a "<field> is required" error to the list if the value is null or blank.
   *
   * @param value  the field value to check
   * @param field  the field name used in the error message
   * @param errors the list to append the error to
   */
  private void requirePresent(String value, String field, List<String> errors) {
    if (!isPresent(value)) {
      errors.add(field + " is required");
    }
  }

  /**
   * Returns true if the value is not null and contains at least one non-whitespace character.
   */
  private boolean isPresent(String value) {
    return value != null && !value.isBlank();
  }

  /**
   * Returns true if the value can be parsed as an ISO-8601 UTC instant
   * (e.g. 2026-09-27T05:00:00Z), false otherwise.
   */
  private boolean isIsoInstant(String value) {
    try {
      Instant.parse(value);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  /**
   * Returns true if the value is a syntactically valid URI with an http or https
   * scheme and a non-null host, false otherwise.
   *
   * uri.getScheme() returns the part before "://" (e.g. "http" or "https") to match against
   * uri.getHost() returns the domain name
   */
  private boolean isHttpUrl(String value) {
    try {
      URI uri = new URI(value);
      return ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) && uri.getHost() != null;
    } catch (URISyntaxException e) {
      return false;
    }
  }
}
