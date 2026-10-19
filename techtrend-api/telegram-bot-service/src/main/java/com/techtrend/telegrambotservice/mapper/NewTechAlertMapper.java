package com.techtrend.telegrambotservice.mapper;

import com.techtrend.telegrambotservice.dto.NewTechAlertDto;
import com.techtrend.telegrambotservice.dto.TechnologyLookup;
import com.techtrendradar.avro.NewTechDetected;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NewTechAlertMapper {
  // Category code needs a MySQL lookup (not wired yet), so it stays null for now.
  public NewTechAlertDto toDto(NewTechDetected newTechDetected, TechnologyLookup technologyLookup) {
    return NewTechAlertDto.builder()
                          .technologyId(newTechDetected.getTechnologyId())
                          .technologyName(technologyLookup.getCanonicalName())
                          .categoryCode(technologyLookup.getCategoryCode())
                          .source(newTechDetected.getSource().toString())
                          .url(newTechDetected.getUrl().toString())
                          .build();
  }
}
