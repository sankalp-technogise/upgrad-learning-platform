package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.technogise.upgrad.backend.config.OtpRateLimitConfig;
import com.technogise.upgrad.backend.dto.AuthResponse;
import com.technogise.upgrad.backend.entity.OtpVerification;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.exception.RateLimitExceededException;
import com.technogise.upgrad.backend.repository.OtpRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private OtpRepository otpRepository;
  @Mock private EmailService emailService;
  @Mock private JwtService jwtService;
  @Mock private OtpRateLimitConfig rateLimitConfig;

  @InjectMocks private AuthService authService;

  @Test
  @SuppressWarnings("null")
  void shouldGenerateOtpAndSendEmail() {
    final String email = "test@example.com";

    // Mock rate limit config
    when(rateLimitConfig.getTimeWindowSeconds()).thenReturn(90);
    when(rateLimitConfig.getMaxAttempts()).thenReturn(3);
    when(otpRepository.findByEmailAndCreatedAtAfterOrderByCreatedAtAsc(eq(email), any()))
        .thenReturn(java.util.List.of());

    authService.generateOtp(email);

    verify(otpRepository, times(1)).save(any(OtpVerification.class));
    verify(emailService, times(1)).sendOtp(eq(email), anyString());
  }

  @Test
  @SuppressWarnings("null")
  void shouldInvalidatePreviousOtpWhenGeneratingNew() {
    final String email = "test@example.com";

    // Create a mock previous OTP
    final OtpVerification previousOtp =
        OtpVerification.builder()
            .id(UUID.randomUUID())
            .email(email)
            .otpHash("old-hash")
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .verified(false)
            .build();

    // Mock repository to return the previous OTP
    when(otpRepository.findAllByEmailAndVerified(email, false))
        .thenReturn(java.util.List.of(previousOtp));

    // Mock rate limit config
    when(rateLimitConfig.getTimeWindowSeconds()).thenReturn(90);
    when(rateLimitConfig.getMaxAttempts()).thenReturn(3);
    when(otpRepository.findByEmailAndCreatedAtAfterOrderByCreatedAtAsc(eq(email), any()))
        .thenReturn(java.util.List.of());

    // Generate new OTP
    authService.generateOtp(email);

    // Verify that previous OTP was marked as verified (invalidated)
    verify(otpRepository, times(2))
        .save(any(OtpVerification.class)); // 1 for invalidation, 1 for new OTP
    assertEquals(true, previousOtp.getVerified());
    verify(emailService, times(1)).sendOtp(eq(email), anyString());
  }

  @Test
  @SuppressWarnings("null")
  void shouldThrowRateLimitExceptionWhenMaxAttemptsExceeded() {
    final String email = "test@example.com";
    final LocalDateTime now = LocalDateTime.now();

    // Create 3 recent OTP requests (within 90 seconds)
    final java.util.List<OtpVerification> recentRequests =
        java.util.List.of(
            OtpVerification.builder().email(email).createdAt(now.minusSeconds(60)).build(),
            OtpVerification.builder().email(email).createdAt(now.minusSeconds(30)).build(),
            OtpVerification.builder().email(email).createdAt(now.minusSeconds(10)).build());

    // Mock rate limit config
    when(rateLimitConfig.getTimeWindowSeconds()).thenReturn(90);
    when(rateLimitConfig.getMaxAttempts()).thenReturn(3);
    when(rateLimitConfig.getCooldownMinutes()).thenReturn(2);
    when(otpRepository.findByEmailAndCreatedAtAfterOrderByCreatedAtAsc(eq(email), any()))
        .thenReturn(recentRequests);

    // Should throw exception
    final RateLimitExceededException exception =
        assertThrows(RateLimitExceededException.class, () -> authService.generateOtp(email));

    assertTrue(exception.getMessage().contains("Too many OTP requests"));
    assertTrue(exception.getMessage().contains("seconds"));
  }

  @Test
  @SuppressWarnings("null")
  void shouldAllowOtpGenerationAfterCooldownPeriod() {
    final String email = "test@example.com";
    final LocalDateTime now = LocalDateTime.now();

    // Create 3 requests, but oldest is beyond cooldown (> 90 seconds + 2 minutes)
    final java.util.List<OtpVerification> oldRequests =
        java.util.List.of(
            OtpVerification.builder().email(email).createdAt(now.minusMinutes(5)).build(),
            OtpVerification.builder().email(email).createdAt(now.minusMinutes(4)).build(),
            OtpVerification.builder().email(email).createdAt(now.minusMinutes(3)).build());

    // Mock rate limit config
    when(rateLimitConfig.getTimeWindowSeconds()).thenReturn(90);
    when(rateLimitConfig.getMaxAttempts()).thenReturn(3);
    when(rateLimitConfig.getCooldownMinutes()).thenReturn(2);
    when(otpRepository.findByEmailAndCreatedAtAfterOrderByCreatedAtAsc(eq(email), any()))
        .thenReturn(oldRequests);
    when(otpRepository.findAllByEmailAndVerified(email, false)).thenReturn(java.util.List.of());

    // Should NOT throw exception
    assertDoesNotThrow(() -> authService.generateOtp(email));
    verify(emailService, times(1)).sendOtp(eq(email), anyString());
  }

  @Test
  @SuppressWarnings("null")
  void shouldLoginSuccessfullyWithValidOtp() {
    final String email = "test@example.com";
    final String otp = "123456";
    final UUID userId = UUID.randomUUID();
    final String token = "jwt-token";
    final String hashedOtp = hashOtp(otp);

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(hashedOtp)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .verified(false)
            .build();

    final User user = User.builder().id(userId).email(email).build();

    when(otpRepository.findFirstByEmailOrderByCreatedAtDesc(email))
        .thenReturn(Optional.of(verification));
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(jwtService.generateToken(userId, email)).thenReturn(token);
    when(rateLimitConfig.getMaxVerificationAttempts()).thenReturn(5);

    final AuthResponse response = authService.login(email, otp);

    assertNotNull(response);
    assertEquals(token, response.token());
    assertEquals(email, response.user().email());
    verify(otpRepository, times(1)).save(verification); // Verified attempts/flag update
  }

  @Test
  @SuppressWarnings("null")
  void shouldCreateUserIfNotExistsOnLogin() {
    final String email = "newuser@example.com";
    final String otp = "654321";
    final UUID userId = UUID.randomUUID();
    final String token = "new-jwt-token";
    final String hashedOtp = hashOtp(otp);

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(hashedOtp)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .verified(false)
            .build();

    final User newUser = User.builder().id(userId).email(email).build();

    when(otpRepository.findFirstByEmailOrderByCreatedAtDesc(email))
        .thenReturn(Optional.of(verification));
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(newUser);
    when(jwtService.generateToken(userId, email)).thenReturn(token);
    when(rateLimitConfig.getMaxVerificationAttempts()).thenReturn(5);

    final AuthResponse response = authService.login(email, otp);

    assertNotNull(response);
    assertEquals(token, response.token());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @SuppressWarnings("null")
  void shouldThrowExceptionForInvalidOtp() {
    final String email = "test@example.com";
    final String otp = "wrong-otp";
    final String correctOtpHash = hashOtp("correct-otp");

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(correctOtpHash)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .verified(false)
            .build();

    when(otpRepository.findFirstByEmailOrderByCreatedAtDesc(email))
        .thenReturn(Optional.of(verification));
    when(rateLimitConfig.getMaxVerificationAttempts()).thenReturn(5);

    final AuthenticationException exception =
        assertThrows(AuthenticationException.class, () -> authService.login(email, otp));
    assertEquals("Invalid OTP", exception.getMessage());
    verify(userRepository, never()).save(any());
    verify(otpRepository, times(1)).save(verification); // Updates attempts
  }

  @Test
  void shouldThrowExceptionForExpiredOtp() {
    final String email = "test@example.com";
    final String otp = "123456";
    final String hashedOtp = hashOtp(otp);
    assertNotNull(hashedOtp);

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(hashedOtp)
            .expiresAt(LocalDateTime.now().minusMinutes(1))
            .attempts(0)
            .verified(false)
            .build();

    when(otpRepository.findFirstByEmailOrderByCreatedAtDesc(email))
        .thenReturn(Optional.of(verification));

    final AuthenticationException exception =
        assertThrows(AuthenticationException.class, () -> authService.login(email, otp));
    assertEquals("OTP Expired", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenMaxAttemptsReached() {
    final String email = "test@example.com";
    final String otp = "123456";
    final String hashedOtp = hashOtp(otp);

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(hashedOtp)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(5) // Max attempts reached
            .verified(false)
            .build();

    when(otpRepository.findFirstByEmailOrderByCreatedAtDesc(email))
        .thenReturn(Optional.of(verification));
    when(rateLimitConfig.getMaxVerificationAttempts()).thenReturn(5);

    final AuthenticationException exception =
        assertThrows(AuthenticationException.class, () -> authService.login(email, otp));
    assertEquals("Too many attempts", exception.getMessage());
  }

  @Test
  @SuppressWarnings("null")
  void shouldIncrementAttemptsAndThrowExceptionOnFailure() {
    final String email = "test@example.com";
    final String otp = "wrong-otp";
    final String correctOtpHash = hashOtp("correct-otp");

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(correctOtpHash) // Mismatch
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .verified(false)
            .build();

    when(otpRepository.findFirstByEmailOrderByCreatedAtDesc(email))
        .thenReturn(Optional.of(verification));
    when(rateLimitConfig.getMaxVerificationAttempts()).thenReturn(5);

    final AuthenticationException exception =
        assertThrows(AuthenticationException.class, () -> authService.login(email, otp));
    assertEquals("Invalid OTP", exception.getMessage());

    verify(otpRepository, times(1)).save(verification);
    assertEquals(1, verification.getAttempts());
  }

  private String hashOtp(String otp) {
    try {
      java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(otp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      return java.util.HexFormat.of().formatHex(encodedhash);
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
