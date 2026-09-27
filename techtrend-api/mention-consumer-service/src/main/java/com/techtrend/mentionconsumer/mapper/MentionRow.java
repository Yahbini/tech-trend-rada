package com.techtrend.mentionconsumer.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MentionRow (
    String source,
    LocalDate timeBucket,
    UUID mentionId,
    String externalId,
    String rawText,
    String url,
    Instant crawledAt
) {}
