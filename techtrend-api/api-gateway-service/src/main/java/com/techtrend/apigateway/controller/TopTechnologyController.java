package com.techtrend.apigateway.controller;

import com.techtrend.apigateway.dto.request.TopTechnologiesRequest;
import com.techtrend.apigateway.dto.response.TopTechnologyResponse;
import com.techtrend.apigateway.service.TopTechnologyService;
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
public class TopTechnologyController {
  TopTechnologyService topTechnologyService;

  @GetMapping("/top")
  public List<TopTechnologyResponse> findTopTechnologies(@Valid TopTechnologiesRequest request) {
    return topTechnologyService.findTop(request);
  }
}
