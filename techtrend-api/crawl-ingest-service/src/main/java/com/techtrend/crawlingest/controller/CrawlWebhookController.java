package com.techtrend.crawlingest.controller;

import com.techtrend.crawlingest.dto.request.CrawlWebhookRequest;
import com.techtrend.crawlingest.service.CrawlIngestService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CrawlWebhookController {
    CrawlIngestService crawlIngestService;

    @PostMapping("/crawl")
    public ResponseEntity<String> receiveCrawlPayload(@RequestBody CrawlWebhookRequest payload) {
        log.info("Received crawl payload: {}", payload);
        crawlIngestService.ingest(payload);
        return ResponseEntity.ok("received");
    }
}
