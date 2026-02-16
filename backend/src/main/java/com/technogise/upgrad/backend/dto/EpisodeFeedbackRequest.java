package com.technogise.upgrad.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record EpisodeFeedbackRequest(
    @NotNull UUID contentId, @NotNull @Pattern(regexp = "HELPFUL|NOT_HELPFUL") String feedback) {}
