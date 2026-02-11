package com.technogise.upgrad.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContentDetailDto(
    UUID id,
    String title,
    String description,
    String thumbnailUrl,
    String videoUrl,
    String category,
    Integer episodeNumber,
    Integer durationSeconds,
    LocalDateTime createdAt) {}
