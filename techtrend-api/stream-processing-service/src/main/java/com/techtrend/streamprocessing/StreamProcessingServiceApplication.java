package com.techtrend.streamprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class StreamProcessingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(StreamProcessingServiceApplication.class, args);
  }

}
