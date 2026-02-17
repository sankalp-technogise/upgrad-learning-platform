package com.technogise.upgrad.backend.dto;

import com.technogise.upgrad.backend.constants.Feedback;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EpisodeFeedbackRequest(@NotNull UUID contentId, @NotNull Feedback feedback) {}
