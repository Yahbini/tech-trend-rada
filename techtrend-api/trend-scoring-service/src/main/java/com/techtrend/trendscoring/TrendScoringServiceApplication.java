package com.techtrend.trendscoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TrendScoringServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(TrendScoringServiceApplication.class, args);
  }
}