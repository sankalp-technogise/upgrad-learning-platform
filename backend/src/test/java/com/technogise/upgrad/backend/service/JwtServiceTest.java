package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  @Test
  void shouldGenerateTokenWithCompliantSecret() {
    String longSecret = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^";
    JwtService jwtService = new JwtService(longSecret, 100000);

    String token = jwtService.generateToken(UUID.randomUUID(), "test@example.com");

    assertNotNull(token);
  }
}
