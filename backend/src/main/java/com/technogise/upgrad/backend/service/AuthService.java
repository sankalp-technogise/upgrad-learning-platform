package com.technogise.upgrad.backend.service;

import com.technogise.upgrad.backend.config.OtpRateLimitConfig;
import com.technogise.upgrad.backend.dto.AuthResponse;
import com.technogise.upgrad.backend.dto.UserDto;
import com.technogise.upgrad.backend.entity.OtpVerification;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.exception.RateLimitExceededException;
import com.technogise.upgrad.backend.repository.OtpRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final OtpRepository otpRepository;
  private final EmailService emailService;
  private final JwtService jwtService;
  private final OtpRateLimitConfig rateLimitConfig;
  private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

  @Transactional
  @SuppressWarnings("null")
  public void generateOtp(final String email) {
    // Check rate limit
    checkRateLimit(email);

    // Invalidate all previous unverified OTPs for this email
    final java.util.List<OtpVerification> previousOtps =
        otpRepository.findAllByEmailAndVerified(email, false);
    previousOtps.forEach(
        otp -> {
          otp.setVerified(true);
          otpRepository.save(otp);
        });

    // Generate new OTP
    final String otp = new DecimalFormat("000000").format(RANDOM.nextInt(1_000_000));
    final String otpHash = hashOtp(otp);

    final OtpVerification verification =
        OtpVerification.builder()
            .email(email)
            .otpHash(otpHash)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .attempts(0)
            .verified(false)
            .build();
    otpRepository.save(verification);
    emailService.sendOtp(email, otp);
  }

  @Transactional
  @SuppressWarnings("null")
  public AuthResponse login(final String email, final String otp) {
    final OtpVerification verification =
        otpRepository
            .findFirstByEmailOrderByCreatedAtDesc(email)
            .orElseThrow(() -> new AuthenticationException("Invalid OTP or Email"));

    if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new AuthenticationException("OTP Expired");
    }

    if (verification.getAttempts() >= rateLimitConfig.getMaxVerificationAttempts()) {
      throw new AuthenticationException("Too many attempts");
    }

    if (verification.getVerified()) {
      incrementAttempts(verification);
      throw new AuthenticationException("Invalid OTP");
    }

    final String inputHash = hashOtp(otp);
    if (!verification.getOtpHash().equals(inputHash)) {
      incrementAttempts(verification);
      if (verification.getAttempts() >= rateLimitConfig.getMaxVerificationAttempts()) {
        throw new AuthenticationException("Too many attempts");
      }
      throw new AuthenticationException("Invalid OTP");
    }

    verification.setVerified(true);
    otpRepository.save(verification);

    final User user =
        userRepository
            .findByEmail(email)
            .orElseGet(() -> userRepository.save(User.builder().email(email).build()));

    final String token = jwtService.generateToken(user.getId(), user.getEmail());

    return new AuthResponse(
        token, new UserDto(user.getId(), user.getEmail(), user.getOnboardingCompleted()));
  }

  @Transactional(readOnly = true)
  public UserDto getUser(final String email) {
    return userRepository
        .findByEmail(email)
        .map(user -> new UserDto(user.getId(), user.getEmail(), user.getOnboardingCompleted()))
        .orElseThrow(() -> new AuthenticationException("User not found"));
  }

  private String hashOtp(String otp) {
    try {
      java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(otp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      return java.util.HexFormat.of().formatHex(encodedhash);
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }

  private void incrementAttempts(OtpVerification verification) {
    verification.setAttempts(verification.getAttempts() + 1);
    otpRepository.save(verification);
  }

  private void checkRateLimit(final String email) {
    final LocalDateTime windowStart =
        LocalDateTime.now().minusSeconds(rateLimitConfig.getTimeWindowSeconds());

    final java.util.List<OtpVerification> recentRequests =
        otpRepository.findByEmailAndCreatedAtAfterOrderByCreatedAtAsc(email, windowStart);

    if (recentRequests.size() >= rateLimitConfig.getMaxAttempts()) {
      final LocalDateTime oldestRequest = recentRequests.get(0).getCreatedAt();
      final LocalDateTime cooldownEnd =
          oldestRequest
              .plusSeconds(rateLimitConfig.getTimeWindowSeconds())
              .plusMinutes(rateLimitConfig.getCooldownMinutes());

      if (LocalDateTime.now().isBefore(cooldownEnd)) {
        final long secondsRemaining =
            java.time.Duration.between(LocalDateTime.now(), cooldownEnd).getSeconds();
        throw new RateLimitExceededException(
            "Too many OTP requests. Please try again in " + secondsRemaining + " seconds.");
      }
    }
  }
}
