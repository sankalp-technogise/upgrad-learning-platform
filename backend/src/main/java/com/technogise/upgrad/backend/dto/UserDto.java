package com.technogise.upgrad.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record UserDto(@JsonProperty("id") UUID userId, String email) {
}
