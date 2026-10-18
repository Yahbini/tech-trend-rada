package com.techtrend.telegrambotservice.formatter;

import com.techtrend.telegrambotservice.dto.NewTechAlertDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Builds Telegram message text for new-tech alerts; pure formatting, no DB or API calls.
 */
@Component
public class NewTechAlertFormatter {
  private static final String UNCATEGORIZED = "uncategorized";
  private static final String TEMPLATE = """
        🆕 New technology: <b>%s</b>
        📂 Category: %s
        📡 Source: %s
        🔗 <a href="%s">Open link</a>""";

  // Telegram HTML only knows &lt; &gt; &amp; &quot;; '&' first so produced entities aren't re-escaped.
  private static String escapeHtml(String value) {
    return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
  }

  public String format(NewTechAlertDto dto) {
    String name = Objects.requireNonNullElse(dto.getTechnologyName(), "#" + dto.getTechnologyId());
    String category = Objects.requireNonNullElse(dto.getCategoryCode(), UNCATEGORIZED);

    return TEMPLATE.formatted(escapeHtml(name), escapeHtml(category), escapeHtml(dto.getSource()),
          escapeHtml(dto.getUrl()));
  }
}
