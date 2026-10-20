package com.techtrend.essinkconsumerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EsSinkConsumerServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(EsSinkConsumerServiceApplication.class, args);
  }
}