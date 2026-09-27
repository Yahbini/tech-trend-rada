package com.techtrend.crawlingest.entities;

import com.techtrend.crawlingest.dto.request.CrawlWebhookRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "raw_crawl_log")
public class RawCrawlLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_id")
  private Source source;

  @Size(max = 255)
  @Column(name = "external_id")
  private String externalId;

  @Column(name = "raw_payload")
  @JdbcTypeCode(SqlTypes.JSON)
  private CrawlWebhookRequest rawPayload;

  @Column(name = "crawled_at")
  private Instant crawledAt;

}