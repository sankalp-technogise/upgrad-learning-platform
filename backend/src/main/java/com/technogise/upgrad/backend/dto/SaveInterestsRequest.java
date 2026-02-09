package com.technogise.upgrad.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.lang.NonNull;

/**
 * Request to save user's selected interests.
 *
 * @param interestNames List of interest identifiers (e.g., "PYTHON_PROGRAMMING", "DATA_SCIENCE")
 */
public record SaveInterestsRequest(@NonNull @NotEmpty List<@NotNull String> interestNames) {}
