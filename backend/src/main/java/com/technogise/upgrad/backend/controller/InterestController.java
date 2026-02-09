package com.technogise.upgrad.backend.controller;

import com.technogise.upgrad.backend.dto.InterestDTO;
import com.technogise.upgrad.backend.dto.SaveInterestsRequest;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.service.InterestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterestController {
  private final InterestService interestService;
  private final UserRepository userRepository;

  @GetMapping("/interests")
  public ResponseEntity<List<InterestDTO>> getAllInterests() {
    return ResponseEntity.ok(interestService.getAllInterests());
  }

  @PostMapping("/user/interests")
  public ResponseEntity<Void> saveUserInterests(
      @Valid @RequestBody final SaveInterestsRequest request, Authentication authentication) {
    // Extract user email from authenticated principal (populated by
    // JwtAuthenticationFilter)
    final String email = (String) authentication.getPrincipal();
    final User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AuthenticationException("User not found"));

    interestService.saveUserInterests(user, request.interestNames());
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
