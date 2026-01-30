package com.technogise.upgrad.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "otp_verifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {
  @Id
  @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
  private java.util.UUID id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  @Setter
  private String otpHash;

  @Column(nullable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  @Setter
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  @Builder.Default
  @Setter
  private Integer attempts = 0;

  @Column(nullable = false)
  @Builder.Default
  @Setter
  private Boolean verified = false;
}
