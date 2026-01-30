package com.technogise.upgrad.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "otp.rate-limit")
@Getter
@Setter
public class OtpRateLimitConfig {
  private int maxAttempts = 3;
  private int timeWindowSeconds = 90;
  private int cooldownMinutes = 2;
  private int maxVerificationAttempts = 5;
}
