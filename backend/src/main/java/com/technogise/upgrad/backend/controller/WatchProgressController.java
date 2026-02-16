package com.technogise.upgrad.backend.controller;

import com.technogise.upgrad.backend.dto.EpisodeFeedbackRequest;
import com.technogise.upgrad.backend.dto.WatchProgressRequest;
import com.technogise.upgrad.backend.dto.WatchProgressResponse;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.service.WatchProgressService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/watch-progress")
@RequiredArgsConstructor
public class WatchProgressController {

  private final WatchProgressService watchProgressService;
  private final UserRepository userRepository;

  @PutMapping
  public ResponseEntity<Void> saveProgress(
      Authentication authentication, @Valid @RequestBody WatchProgressRequest request) {
    final User user = resolveUser(authentication);
    watchProgressService.saveProgress(user.getId(), request);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/feedback")
  public ResponseEntity<Void> saveFeedback(
      Authentication authentication, @Valid @RequestBody EpisodeFeedbackRequest request) {
    final User user = resolveUser(authentication);
    watchProgressService.saveFeedback(user.getId(), request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{contentId}")
  public ResponseEntity<WatchProgressResponse> getProgress(
      Authentication authentication, @PathVariable UUID contentId) {
    final User user = resolveUser(authentication);
    return watchProgressService
        .getProgress(user.getId(), contentId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  private User resolveUser(Authentication authentication) {
    final String email = authentication.getName();
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new AuthenticationException("User not found"));
  }
}
