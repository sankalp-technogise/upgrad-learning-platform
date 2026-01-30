package com.technogise.upgrad.backend.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

  @Mock private JavaMailSender emailSender;

  private SmtpEmailService smtpEmailService;

  @BeforeEach
  void setUp() {
    smtpEmailService = new SmtpEmailService(emailSender, "test@example.com");
  }

  @Test
  @SuppressWarnings("null") // SimpleMailMessage argument
  void shouldSendOtpEmail() {
    final String to = "user@example.com";
    final String otp = "123456";

    smtpEmailService.sendOtp(to, otp);

    verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
  }
}
