package com.techtrend.crawlingest.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "alias_map")
public class AliasMap {
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 255)
  @NotNull
  @Column(name = "alias", nullable = false)
  private String alias;

  @Size(max = 255)
  @NotNull
  @Column(name = "canonical_name", nullable = false)
  private String canonicalName;

  @NotNull
  @Column(name = "case_sensitive", nullable = false)
  private Boolean caseSensitive;
}