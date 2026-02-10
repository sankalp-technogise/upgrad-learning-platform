package com.technogise.upgrad.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contents")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  private String description;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @Column(name = "video_url")
  private String videoUrl;

  @Column(nullable = false, length = 100)
  private String category;

  @Column(name = "episode_number")
  private Integer episodeNumber;

  @Column(name = "duration_seconds")
  private Integer durationSeconds;

  @Column(nullable = false, updatable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
