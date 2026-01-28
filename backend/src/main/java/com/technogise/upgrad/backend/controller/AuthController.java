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

    // Create HttpOnly Cookie
    org.springframework.http.ResponseCookie cookie =
        org.springframework.http.ResponseCookie.from("token", response.token())
            .httpOnly(true)
            .secure(false) // Set to true in production with HTTPS
            .path("/")
            .maxAge(24 * 60 * 60) // 1 day
            .sameSite("Strict")
            .build();

    // Return response with cookie
    return ResponseEntity.ok()
        .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString())
        .body(new AuthResponse("", response.user())); // Don't send token in body
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    org.springframework.http.ResponseCookie cookie =
        org.springframework.http.ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0) // Expire immediately
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
        .header(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
  }

  @org.springframework.web.bind.annotation.GetMapping("/me")
  public ResponseEntity<com.technogise.upgrad.backend.dto.UserDto> me(
      java.security.Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.ok(authService.getUser(principal.getName()));
  }
}
