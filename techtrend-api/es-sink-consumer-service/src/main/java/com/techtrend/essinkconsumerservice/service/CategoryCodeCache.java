package com.techtrend.essinkconsumerservice.service;

import com.techtrend.essinkconsumerservice.entities.Category;
import com.techtrend.essinkconsumerservice.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * category_id → code for ES documents; loaded once at startup, unknown ids are looked up again (category added later).
 */
@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryCodeCache {
  CategoryRepository categoryRepository;
  Map<Long, String> codes = new ConcurrentHashMap<>();

  // Runs before Kafka listener containers start, so the first record never sees an empty cache.
  @PostConstruct
  void load() {
    categoryRepository.findAll().forEach(c -> codes.put(c.getId(), c.getCode()));
    log.info("Category cache loaded: {} entries", codes.size());
  }

  public String codeOf(Long categoryId) {
    if (categoryId == null) {
      return null;
    }

    String cached = codes.get(categoryId);
    if (cached != null) {
      return cached;
    }

    // Misses are not cached, so a category inserted later is still found on the next record.
    return categoryRepository.findById(categoryId)
                             .map(Category::getCode)
                             .map(code -> {
                               codes.put(categoryId, code);
                               log.info("Category cache miss resolved: id={} code={}", categoryId, code);
                               return code;
                             })
                             .orElse(null);
  }
}
