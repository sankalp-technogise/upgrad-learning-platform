package com.technogise.upgrad.backend.dto;

import java.util.UUID;

public record ContinueWatchingDto(
    UUID contentId,
    String title,
    String description,
    String thumbnailUrl,
    int progressPercent,
    String category,
    Integer episodeNumber,
    int lastWatchedPosition) {}
