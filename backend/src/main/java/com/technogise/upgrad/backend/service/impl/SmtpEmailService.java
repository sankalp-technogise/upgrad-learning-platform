package com.technogise.upgrad.backend.service.impl;

import com.technogise.upgrad.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "smtp")
public class SmtpEmailService implements EmailService {

  private final JavaMailSender emailSender;
  private final String fromEmail;

  public SmtpEmailService(
      final JavaMailSender emailSender, @Value("${app.email.from}") final String fromEmail) {
    this.emailSender = emailSender;
    this.fromEmail = fromEmail;
  }

  @Override
  public void sendOtp(final String toEmail, final String otp) {
    final SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromEmail);
    message.setTo(toEmail);
    message.setSubject("Your OTP Code");
    message.setText("Your OTP code is: " + otp);
    emailSender.send(message);
  }
}
