package com.techtrend.apigateway.controller;

import com.techtrend.apigateway.dto.request.SearchTechnologiesRequest;
import com.techtrend.apigateway.dto.response.SearchTechnologyResponse;
import com.techtrend.apigateway.service.SearchTechnologyService;
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
public class SearchTechnologyController {
  SearchTechnologyService searchTechnologyService;

  @GetMapping("/search")
  public List<SearchTechnologyResponse> search(@Valid SearchTechnologiesRequest request) {
    return searchTechnologyService.search(request);
  }
}
