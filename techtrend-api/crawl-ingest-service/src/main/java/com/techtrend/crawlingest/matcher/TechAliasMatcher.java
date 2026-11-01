package com.techtrend.crawlingest.matcher;

import com.techtrend.crawlingest.entities.AliasMap;
import com.techtrend.crawlingest.repository.AliasMapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Finds known technologies in raw text using the alias_map table, loaded once at startup.
 * Longest alias wins, so "Node.js" is not also counted as "JS".
 */
@Slf4j
@Component
public class TechAliasMatcher {
  //  Character is not a letter or a number
  //  Do not use \b, as \b distinguishes between letters/numbers and other characters
  private static final String WORD_START = "(?<![\\p{L}\\p{N}])";
  private static final String WORD_END = "(?![\\p{L}\\p{N}])";
  // Empty alias_map would build "()", which matches everywhere.
  private static final Pattern NEVER_MATCHES = Pattern.compile("(?!)");

  private final Map<String, AliasMap> caseSensitiveAliases = new HashMap<>();
  private final Map<String, AliasMap> caseInsensitiveAliases = new HashMap<>();
  private final Pattern pattern;

  /**
   * Constructor: loads data once when the app starts
   *
   * A constructor performs two tasks
   * - Split the aliases into two maps for future lookups.
   * - Pre-compile a large regular expression. Compiling a regular expression is resource-intensive, so do it once and use it for all mentions.
   * (Dựng sẵn một regex lớn. Compile regex tốn chi phí, nên làm một lần rồi dùng cho mọi mention.)
   *
   * Example: caseSensitiveAliases   = { "Go" → Go, "React" → React }
   *          caseInsensitiveAliases = { "golang" → Go, "js" → JavaScript, "node.js" → Node.js, "react native", React Native }
   * */
  public TechAliasMatcher(AliasMapRepository aliasMapRepository) {
    List<AliasMap> entries = aliasMapRepository.findAll();
    for (AliasMap entry : entries) {
      String alias = entry.getAlias().strip();
      if (isCaseSensitive(entry)) {
        caseSensitiveAliases.put(alias, entry);
      } else {
        caseInsensitiveAliases.put(alias.toLowerCase(Locale.ROOT), entry);
      }
    }
    this.pattern = entries.isEmpty() ? NEVER_MATCHES : compile(entries);
    log.info("Loaded {} aliases from alias_map", entries.size());
  }

  /**
   * - matcher.find(): each time this is called, the regex searches for the next match, starting from where the previous match ended. When there are no further matches, it returns false and the loop stops.
   * (matcher.find(): mỗi lần gọi, regex tìm lần khớp tiếp theo, bắt đầu từ sau lần khớp trước. Hết thì trả về false và vòng lặp dừng)
   *
   * - matcher.group(1): the text that just matched, contained within the parentheses ( … ) of the regex.
   * (matcher.group(1): đoạn text vừa khớp, nằm trong cặp ngoặc ( … ) của regex)
   *
   * - if (entry != null): a safeguard in case the two maps do not match. If everything is correct, the null branch will never be executed.
   * */
  public List<AliasMap> findMatches(String rawText) {
    if (rawText == null || rawText.isBlank()) {
      return List.of();
    }
    // LinkedHashSet: keep first-seen order, drop repeats ("Kafka ... kafka" → one entry).
    Set<AliasMap> matches = new LinkedHashSet<>();
    Matcher matcher = pattern.matcher(normalize(rawText));
    while (matcher.find()) {
      AliasMap entry = lookup(matcher.group(1));
      if(entry != null) {
        matches.add(entry);
      }
    }

    return List.copyOf(matches);
  }

  /**
   * Distinct canonical names mentioned in the text, in order of first appearance.
   * Several aliases of one technology ("kafka", "Apache Kafka") count once.
   */
  public List<String> findCanonicalNames(String rawText) {
    return findMatches(rawText).stream()
                               .map(AliasMap::getCanonicalName)
                               .distinct()
                               .toList();
  }



  /**
   * lookup: find the AliasMap based on a matching text string
   *
   * Example
   * rawText =   "I use node.js & React Native, let's go! React rocks"
   * normalize → "I use node.js & React Native, let's go! React rocks"
   * find() #1 → "node.js"       → lookup → Node.js      → matches = [Node.js]
   * find() #2 → "React Native"  → lookup → React Native → matches = [Node.js, React Native]
   *            ("React" không bị khớp riêng vì cả cụm "React Native" đã được tính)
   *             "go" → bỏ qua (Go là TRUE, chỉ khớp "Go" viết hoa)
   * find() #3 → "React"         → lookup → React        → matches = [Node.js, React Native, React]
   * find() #4 → false → dừng
   * */
  private AliasMap lookup(String matchedText) {
    AliasMap entry = caseSensitiveAliases.get(matchedText);
    return entry != null ? entry : caseInsensitiveAliases.get(matchedText.toLowerCase(Locale.ROOT));
  }

  private static String normalize(String rawText) {
    return HtmlUtils.htmlUnescape(rawText).replaceAll("\\s+", " ").strip();
  }

  /**
   * compile: combine all aliases into a single regex
   *
   * Longer aliases first: regex alternation takes the first branch that fits, so "Apache Kafka" beats "Kafka".
   * */
  private static Pattern compile(List<AliasMap> entries) {
    String alternatives =
          entries.stream()
                .sorted(Comparator.comparingInt((AliasMap entry) -> entry.getAlias().strip().length()).reversed())
                .map(TechAliasMatcher::toRegex)
                .collect(Collectors.joining("|"));
    return Pattern.compile(WORD_START + "(" + alternatives + ")" + WORD_END);
  }

  /**
   * toRegex: an alias for a regex snippet
   *
   * - The `\Q…\E` pattern quotes the alias so that all characters within it are interpreted literally.
   * - (?iu: … ) enables case-insensitive mode for this pattern only. ‘i’ stands for ‘ignore case’, and ‘u’ means it applies to Unicode characters as well.
   * */
  private static String toRegex(AliasMap entry) {
    String quoted = Pattern.quote(entry.getAlias().strip());
    return isCaseSensitive(entry) ? quoted : "(?iu:" + quoted + ")";
  }

  /**
   * isCaseSensitive: read the safety flag
   * */
  private static boolean isCaseSensitive(AliasMap entry) {
    return Boolean.TRUE.equals(entry.getCaseSensitive());
  }
}
