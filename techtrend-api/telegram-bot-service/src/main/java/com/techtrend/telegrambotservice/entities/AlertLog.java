package com.techtrend.telegrambotservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert_log")
public class AlertLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "technology_id", nullable = false)
  private Long technologyId;

  @Column(name = "canonical_name", nullable = false)
  private String canonicalName;

  @Column(name = "sent_at", nullable = false)
  private Instant sentAt;

  @Column(name = "sent_date", insertable = false, updatable = false)
  private LocalDate sentDate;

  @Column(name = "telegram_message_id", length = 100)
  private String telegramMessageId;

}