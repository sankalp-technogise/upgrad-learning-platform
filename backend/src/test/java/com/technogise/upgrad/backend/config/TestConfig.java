package com.technogise.upgrad.backend.config;

import com.technogise.upgrad.backend.service.EmailService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

  @Bean
  @Primary
  public EmailService emailService() {
    return Mockito.mock(EmailService.class);
  }
}
