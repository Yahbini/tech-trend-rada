package com.techtrend.apigateway.config;

import com.techtrend.apigateway.dto.request.TopSortField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

// Spring's default enum binding is case-sensitive; this lets clients send ?sortBy=velocity.
@Component
public class TopSortFieldConverter implements Converter<String, TopSortField> {
  @Override
  public TopSortField convert(String source) {
    return TopSortField.valueOf(source.trim().toUpperCase(Locale.ROOT));
  }
}
