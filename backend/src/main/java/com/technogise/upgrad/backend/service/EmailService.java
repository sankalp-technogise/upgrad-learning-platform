package com.technogise.upgrad.backend.service;

public interface EmailService {
  void sendOtp(String toEmail, String otp);
}
