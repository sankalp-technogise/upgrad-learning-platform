package com.technogise.upgrad.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record WatchProgressRequest(
    @NotNull UUID contentId,
    @Min(0) @Max(100) int progressPercent,
    @Min(0) int lastWatchedPosition) {}
