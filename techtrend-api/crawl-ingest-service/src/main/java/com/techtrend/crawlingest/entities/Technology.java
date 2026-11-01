package com.techtrend.crawlingest.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "technology")
public class Technology {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 255)
  @NotNull
  @Column(name = "canonical_name", nullable = false)
  private String canonicalName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(name = "first_seen_at")
  private Instant firstSeenAt;

  @Column(name = "last_seen_at")
  private Instant lastSeenAt;

  @ColumnDefault("0")
  @Column(name = "mention_count_total")
  private Integer mentionCountTotal;

  @ColumnDefault("1")
  @Column(name = "is_new")
  private Boolean isNew;

  @ColumnDefault("0")
  @Column(name = "mention_count_last_7d")
  private Integer mentionCountLast7d;

  @ColumnDefault("0")
  @Column(name = "mention_count_prev_7d")
  private Integer mentionCountPrev7d;

  @ColumnDefault("0.0000")
  @Column(name = "mention_velocity", precision = 10, scale = 4)
  private BigDecimal mentionVelocity;

  @CreationTimestamp
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public void increaseMentionCount() {
    mentionCountTotal = (mentionCountTotal == null ? 0 : mentionCountTotal) + 1;
  }
}