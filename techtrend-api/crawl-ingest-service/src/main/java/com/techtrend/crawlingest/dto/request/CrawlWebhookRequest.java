package com.techtrend.crawlingest.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrawlWebhookRequest {
  String source;
  String url;
  String title;
  @JsonProperty("external_id")
  String externalId;
  @JsonProperty("raw_text")
  String rawText;
  @JsonProperty("crawled_at")
  String crawledAt;
}
