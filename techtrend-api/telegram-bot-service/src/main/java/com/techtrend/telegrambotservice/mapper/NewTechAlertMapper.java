package com.techtrend.telegrambotservice.mapper;

import com.techtrend.telegrambotservice.dto.NewTechAlertDto;
import com.techtrendradar.avro.NewTechDetected;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NewTechAlertMapper {
  // Category code needs a MySQL lookup (not wired yet), so it stays null for now.
  public NewTechAlertDto toDto(NewTechDetected newTechDetected) {
    return NewTechAlertDto.builder()
                          .url(newTechDetected.getUrl().toString())
                          .source(newTechDetected.getSource().toString())
                          .technologyId(newTechDetected.getTechnologyId())
                          .technologyName(Objects.toString(newTechDetected.getCanonicalName(), null))
                          .build();
  }
}
