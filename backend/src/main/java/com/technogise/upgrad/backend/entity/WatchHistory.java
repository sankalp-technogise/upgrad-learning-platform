package com.technogise.upgrad.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "watch_history",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "content_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "content_id", nullable = false)
  private Content content;

  @Column(name = "progress_percent", nullable = false)
  @Builder.Default
  private Integer progressPercent = 0;

  @Column(name = "last_watched_at", nullable = false)
  @Builder.Default
  private LocalDateTime lastWatchedAt = LocalDateTime.now();
}
