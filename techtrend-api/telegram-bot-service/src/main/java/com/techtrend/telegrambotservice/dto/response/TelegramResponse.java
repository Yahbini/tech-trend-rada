package com.techtrend.telegrambotservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Common Bot API envelope: result on success; error_code, description and parameters on failure.
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramResponse<T> {
  boolean ok;
  T result;
  @JsonProperty("error_code")
  Integer errorCode;
  String description;
  TelegramResponseParameters parameters;
}
