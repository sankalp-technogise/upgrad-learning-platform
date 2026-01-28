package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.technogise.upgrad.backend.dto.AuthResponse;
import com.technogise.upgrad.backend.entity.OtpVerification;
import com.technogise.upgrad.backend.entity.User;
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

  @InjectMocks private AuthService authService;

  @Test
  @SuppressWarnings("null") // Mock repository save() returns
  void shouldGenerateOtpAndSendEmail() {
    final String email = "test@example.com";

    authService.generateOtp(email);

    verify(otpRepository, times(1)).save(any(OtpVerification.class));
    verify(emailService, times(1)).sendOtp(eq(email), anyString());
  }

  @Test
  @SuppressWarnings("null") // Mock repository returns
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

    final AuthResponse response = authService.login(email, otp);

    assertNotNull(response);
    assertEquals(token, response.token());
    assertEquals(email, response.user().email());
    verify(otpRepository, times(1)).save(verification); // Verified attempts/flag update
  }

  @Test
  @SuppressWarnings("null") // Mock repository returns
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

    final AuthResponse response = authService.login(email, otp);

    assertNotNull(response);
    assertEquals(token, response.token());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @SuppressWarnings("null") // Mock repository returns
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

    final RuntimeException exception =
        assertThrows(RuntimeException.class, () -> authService.login(email, otp));
    assertEquals("Invalid OTP", exception.getMessage());
    verify(userRepository, never()).save(any());
    verify(otpRepository, times(1)).save(verification); // Updates attempts
  }

  @Test
  @SuppressWarnings("null") // Mock repository returns
  void shouldThrowExceptionForExpiredOtp() {
    final String email = "test@example.com";
    final String otp = "123456";
    final String hashedOtp = hashOtp(otp);

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

    final RuntimeException exception =
        assertThrows(RuntimeException.class, () -> authService.login(email, otp));
    assertEquals("OTP Expired", exception.getMessage());
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
