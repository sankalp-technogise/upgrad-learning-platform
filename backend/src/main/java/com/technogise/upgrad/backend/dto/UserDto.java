package com.technogise.upgrad.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record UserDto(@JsonProperty("id") UUID userId, String email, Boolean onboardingCompleted) {

  /** Factory method for tests where onboardingCompleted state is irrelevant to the test. */
  public static UserDto forTest(UUID userId, String email) {
    return new UserDto(userId, email, false);
  }
}
