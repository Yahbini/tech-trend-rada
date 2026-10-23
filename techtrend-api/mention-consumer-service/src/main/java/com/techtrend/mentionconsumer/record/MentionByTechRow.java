package com.techtrend.mentionconsumer.record;

import java.time.Instant;

public record MentionByTechRow (
      Long technologyId,
      String month,
      Instant crawledAt,
      String source,
      String externalId,
      String url,
      String rawText
) {}
