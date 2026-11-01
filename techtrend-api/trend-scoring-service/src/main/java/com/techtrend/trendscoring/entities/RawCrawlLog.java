package com.techtrend.trendscoring.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/**
 * Read-only view of raw_crawl_log (owned by crawl-ingest-service); only the source is needed for counting.
 */
@Getter
@Entity
@Immutable
@Table(name = "raw_crawl_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawCrawlLog {
  @Id
  @Column(name = "id")
  Long id;

  @Column(name = "source_id")
  Long sourceId;

}