package com.technogise.upgrad.backend.service;

import com.technogise.upgrad.backend.dto.AuthResponse;
import com.technogise.upgrad.backend.dto.UserDto;
import com.technogise.upgrad.backend.entity.OtpVerification;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
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
  private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

  @Transactional
  @SuppressWarnings("null")
  public void generateOtp(final String email) {
    final String otp = new DecimalFormat("000000").format(RANDOM.nextInt(999_999));
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

    if (verification.getVerified()) {
      verification.setAttempts(verification.getAttempts() + 1);
      otpRepository.save(verification);
      throw new AuthenticationException("Invalid OTP");
    }

    final String inputHash = hashOtp(otp);
    if (!verification.getOtpHash().equals(inputHash)) {
      verification.setAttempts(verification.getAttempts() + 1);
      otpRepository.save(verification);
      throw new AuthenticationException("Invalid OTP");
    }

    verification.setVerified(true);
    otpRepository.save(verification);

    final User user =
        userRepository
            .findByEmail(email)
            .orElseGet(() -> userRepository.save(User.builder().email(email).build()));

    final String token = jwtService.generateToken(user.getId(), user.getEmail());

    return new AuthResponse(token, new UserDto(user.getId(), user.getEmail()));
  }

  @Transactional(readOnly = true)
  public UserDto getUser(final String email) {
    return userRepository
        .findByEmail(email)
        .map(user -> new UserDto(user.getId(), user.getEmail()))
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
}
