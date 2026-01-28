package com.technogise.upgrad.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private static final long EXPIRATION_TIME = 864_000_000; // 10 days

  private final String secret;

  public JwtService(@Value("${jwt.secret}") final String secret) {
    this.secret = secret;
  }

  public String generateToken(final UUID userId, final String email) {
    return JWT.create()
        .withSubject(userId.toString())
        .withClaim("email", email)
        .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .sign(Algorithm.HMAC512(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
  }
}
