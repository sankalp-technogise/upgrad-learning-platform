package com.technogise.upgrad.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final String secret;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret}") final String secret,
      @Value("${app.jwt.expiration-ms:864000000}") final long expirationMs) {
    this.secret = secret;
    this.expirationMs = expirationMs;
  }

  @PostConstruct
  void validateConfig() {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWT_SECRET is not configured - set app.jwt.secret property");
    }
  }

  public String generateToken(final UUID userId, final String email) {
    return JWT.create()
        .withSubject(userId.toString())
        .withClaim("email", email)
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
        .sign(Algorithm.HMAC512(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
  }

  public com.auth0.jwt.interfaces.DecodedJWT verifyToken(final String token) {
    return JWT.require(Algorithm.HMAC512(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
        .build()
        .verify(token);
  }

  public String getEmailFromToken(final String token) {
    return verifyToken(token).getClaim("email").asString();
  }

  public UUID getUserIdFromToken(final String token) {
    return UUID.fromString(verifyToken(token).getSubject());
  }
}
