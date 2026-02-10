package com.technogise.upgrad.backend.controller;

import com.technogise.upgrad.backend.dto.HomepageSectionsDto;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomepageController {
  private final HomepageService homepageService;
  private final UserRepository userRepository;

  @GetMapping("/homepage")
  public ResponseEntity<HomepageSectionsDto> getHomepage(Authentication authentication) {
    final String email = authentication.getName();
    final User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AuthenticationException("User not found"));

    return ResponseEntity.ok(homepageService.getHomepageSections(user.getId()));
  }
}
