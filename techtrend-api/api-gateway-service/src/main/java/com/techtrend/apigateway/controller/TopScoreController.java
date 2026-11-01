package com.techtrend.apigateway.controller;

import com.techtrend.apigateway.dto.request.TopScoreRequest;
import com.techtrend.apigateway.dto.response.TopScoreResponse;
import com.techtrend.apigateway.service.TopScoreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopScoreController {
  TopScoreService topScoreService;

  @GetMapping("/top")
  public List<TopScoreResponse> findTopScores(@Valid TopScoreRequest request) {
    return topScoreService.findTop(request);
  }
}