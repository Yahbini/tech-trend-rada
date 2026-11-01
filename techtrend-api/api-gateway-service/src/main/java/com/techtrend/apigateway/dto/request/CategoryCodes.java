package com.techtrend.apigateway.dto.request;

/**
 * Category codes accepted by query params; shared so every endpoint validates the same list.
 */
public final class CategoryCodes {
  public static final String PATTERN = "language|database|framework|ai-tool|devops|cloud|messaging|testing|tool|other";
  public static final String MESSAGE =
        "category must be one of: language, database, framework, ai-tool, devops, cloud, messaging, testing, tool, other";

  private CategoryCodes() {
  }
}
