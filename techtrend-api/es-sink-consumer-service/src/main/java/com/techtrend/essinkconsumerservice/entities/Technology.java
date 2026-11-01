package com.techtrend.essinkconsumerservice.entities;

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
 * Read-only view of technology (owned by crawl-ingest-service); fills name/category when the Avro event has none yet.
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

  @Column(name = "category_id")
  Long categoryId;
}