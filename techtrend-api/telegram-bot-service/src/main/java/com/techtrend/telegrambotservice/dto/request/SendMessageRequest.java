package com.techtrend.telegrambotservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Body for Telegram Bot API sendMessage; built by us and only serialized, so no setters.
 */
@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendMessageRequest {
  @JsonProperty("chat_id")
  String chatId;
  String text;
  @JsonProperty("parse_mode")
  String parseMode;
}
