package com.technogise.upgrad.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record SaveInterestsRequest(@NotEmpty List<@NotNull UUID> interestIds) {}
