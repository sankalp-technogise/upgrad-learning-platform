package com.technogise.upgrad.backend.controller;

import com.technogise.upgrad.backend.dto.AuthResponse;
import com.technogise.upgrad.backend.dto.LoginRequest;
import com.technogise.upgrad.backend.dto.OtpRequest;
import com.technogise.upgrad.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
  private final AuthService authService;

  @PostMapping("/otp")
  public ResponseEntity<Void> requestOtp(@Valid @RequestBody final OtpRequest request) {
    authService.generateOtp(request.email());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {
    final AuthResponse response = authService.login(request.email(), request.otp());
    return ResponseEntity.ok(response);
  }
}
