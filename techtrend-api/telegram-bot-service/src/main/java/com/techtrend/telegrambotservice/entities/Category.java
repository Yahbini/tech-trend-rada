package com.techtrend.telegrambotservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

/**
 * Read-only view of category (owned by crawl-ingest-service); only the code is needed for alerts.
 */
@Getter
@Entity
@Immutable
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
  @Id
  @Column(name = "id")
  Long id;

  @Column(name = "code")
  String code;
}