package com.technogise.upgrad.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.technogise.upgrad.backend.config.SecurityConfig;
import com.technogise.upgrad.backend.dto.ContentDto;
import com.technogise.upgrad.backend.dto.ContinueWatchingDto;
import com.technogise.upgrad.backend.dto.HomepageSectionsDto;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.GlobalExceptionHandler;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.security.JwtAuthenticationFilter;
import com.technogise.upgrad.backend.service.HomepageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HomepageController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class HomepageControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private HomepageService homepageService;
  @MockitoBean private UserRepository userRepository;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  private static final String TEST_EMAIL = "test@example.com";
  private static final UUID TEST_USER_ID = UUID.randomUUID();
  private User testUser;

  @BeforeEach
  void setUp() throws ServletException, IOException {
    testUser =
        User.builder()
            .id(TEST_USER_ID)
            .email(TEST_EMAIL)
            .onboardingCompleted(true)
            .createdAt(LocalDateTime.now())
            .build();

    // Default filter behavior: pass through without authentication
    Mockito.doAnswer(
            invocation -> {
              FilterChain chain = invocation.getArgument(2);
              chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
              return null;
            })
        .when(jwtAuthenticationFilter)
        .doFilter(any(), any(), any());
  }

  private void setupAuthenticatedUser() throws ServletException, IOException {
    Mockito.doAnswer(
            invocation -> {
              Authentication auth =
                  new UsernamePasswordAuthenticationToken(
                      TEST_EMAIL, null, Collections.emptyList());
              SecurityContextHolder.getContext().setAuthentication(auth);
              FilterChain chain = invocation.getArgument(2);
              chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
              return null;
            })
        .when(jwtAuthenticationFilter)
        .doFilter(any(), any(), any());
  }

  @Test
  void shouldReturnHomepageSections() throws Exception {
    setupAuthenticatedUser();

    UUID contentId = UUID.randomUUID();
    HomepageSectionsDto sections =
        new HomepageSectionsDto(
            new ContinueWatchingDto(contentId, "Python Intro", "Desc", "thumb.jpg", 50),
            List.of(
                new ContentDto(
                    contentId, "Python Intro", "Desc", "thumb.jpg", "PYTHON_PROGRAMMING")),
            List.of(
                new ContentDto(UUID.randomUUID(), "Design", "Desc", "thumb2.jpg", "UI_UX_DESIGN")));

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
    when(homepageService.getHomepageSections(TEST_USER_ID)).thenReturn(sections);

    mockMvc
        .perform(get("/api/homepage").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.continueWatching.contentId").value(contentId.toString()))
        .andExpect(jsonPath("$.continueWatching.progressPercent").value(50))
        .andExpect(jsonPath("$.recommended").isArray())
        .andExpect(jsonPath("$.recommended[0].title").value("Python Intro"))
        .andExpect(jsonPath("$.exploration").isArray())
        .andExpect(jsonPath("$.exploration[0].title").value("Design"));
  }

  @Test
  void shouldRejectUnauthenticatedRequest() throws Exception {
    mockMvc.perform(get("/api/homepage").with(csrf())).andExpect(status().isForbidden());
  }

  @Test
  void shouldReturnHomepageWithoutContinueWatching() throws Exception {
    setupAuthenticatedUser();

    HomepageSectionsDto sections =
        new HomepageSectionsDto(
            null,
            List.of(
                new ContentDto(
                    UUID.randomUUID(), "Python", "Desc", "thumb.jpg", "PYTHON_PROGRAMMING")),
            List.of());

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
    when(homepageService.getHomepageSections(TEST_USER_ID)).thenReturn(sections);

    mockMvc
        .perform(get("/api/homepage").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.continueWatching").doesNotExist())
        .andExpect(jsonPath("$.recommended[0].title").value("Python"));
  }
}
