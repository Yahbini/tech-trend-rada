package com.techtrend.telegrambotservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

/**
 * Read-only view of technology (owned by crawl-ingest-service); maps only the columns alerts need.
 */
@Getter
@Entity
@Immutable
@Table(name = "technology")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Technology {
  @Id
  @Column(name = "id")
  Long id;

  @Column(name = "canonical_name")
  String canonicalName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  Category category;
}