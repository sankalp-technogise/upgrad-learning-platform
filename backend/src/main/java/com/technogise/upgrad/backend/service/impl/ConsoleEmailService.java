package com.technogise.upgrad.backend.service.impl;

import com.technogise.upgrad.backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.email.provider", havingValue = "console", matchIfMissing = true)
public class ConsoleEmailService implements EmailService {

  @Override
  public void sendOtp(final String toEmail, final String otp) {
    log.info("====================================");
    log.info("Sending OTP to: {}", toEmail);
    log.info("OTP Code: {}", otp);
    log.info("====================================");
  }
}
