package com.techtrend.crawlingest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrawlIngestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlIngestServiceApplication.class, args);
	}

}
