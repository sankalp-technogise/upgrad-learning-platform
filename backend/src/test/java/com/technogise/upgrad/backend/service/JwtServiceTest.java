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

  @Test
  void shouldThrowExceptionWhenSecretIsInvalid() {
    JwtService jwtService = new JwtService(null, 100000);
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class, jwtService::validateConfig);
  }

  @Test
  void shouldVerifyValidToken() {
    String longSecret = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^";
    JwtService jwtService = new JwtService(longSecret, 100000);
    UUID userId = UUID.randomUUID();
    String email = "test@example.com";
    String token = jwtService.generateToken(userId, email);

    com.auth0.jwt.interfaces.DecodedJWT decodedJWT = jwtService.verifyToken(token);

    assertNotNull(decodedJWT);
    org.junit.jupiter.api.Assertions.assertEquals(userId.toString(), decodedJWT.getSubject());
    org.junit.jupiter.api.Assertions.assertEquals(email, decodedJWT.getClaim("email").asString());
  }

  @Test
  void shouldExtractEmailFromToken() {
    String longSecret = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^";
    JwtService jwtService = new JwtService(longSecret, 100000);
    String email = "test@example.com";
    String token = jwtService.generateToken(UUID.randomUUID(), email);

    String extractedEmail = jwtService.getEmailFromToken(token);

    org.junit.jupiter.api.Assertions.assertEquals(email, extractedEmail);
  }

  @Test
  void shouldExtractUserIdFromToken() {
    String longSecret = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^";
    JwtService jwtService = new JwtService(longSecret, 100000);
    UUID userId = UUID.randomUUID();
    String token = jwtService.generateToken(userId, "test@example.com");

    UUID extractedUserId = jwtService.getUserIdFromToken(token);

    org.junit.jupiter.api.Assertions.assertEquals(userId, extractedUserId);
  }

  @Test
  void shouldThrowExceptionForInvalidToken() {
    String longSecret = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^";
    JwtService jwtService = new JwtService(longSecret, 100000);
    String invalidToken = "invalid.token.value";

    org.junit.jupiter.api.Assertions.assertThrows(
        com.auth0.jwt.exceptions.JWTVerificationException.class,
        () -> jwtService.verifyToken(invalidToken));
  }
}
