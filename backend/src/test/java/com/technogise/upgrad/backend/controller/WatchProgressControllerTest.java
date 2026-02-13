package com.technogise.upgrad.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technogise.upgrad.backend.config.SecurityConfig;
import com.technogise.upgrad.backend.dto.WatchProgressRequest;
import com.technogise.upgrad.backend.dto.WatchProgressResponse;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.GlobalExceptionHandler;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.security.JwtAuthenticationFilter;
import com.technogise.upgrad.backend.service.WatchProgressService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WatchProgressController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class WatchProgressControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private WatchProgressService watchProgressService;
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
  void shouldSaveProgressForAuthenticatedUser() throws Exception {
    setupAuthenticatedUser();
    UUID contentId = UUID.randomUUID();
    WatchProgressRequest request = new WatchProgressRequest(contentId, 50, 300);

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

    mockMvc
        .perform(
            put("/api/watch-progress")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(watchProgressService).saveProgress(eq(TEST_USER_ID), any(WatchProgressRequest.class));
  }

  @Test
  void shouldRejectUnauthenticatedSaveProgress() throws Exception {
    UUID contentId = UUID.randomUUID();
    WatchProgressRequest request = new WatchProgressRequest(contentId, 50, 300);

    mockMvc
        .perform(
            put("/api/watch-progress")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldReturnProgressForContent() throws Exception {
    setupAuthenticatedUser();
    UUID contentId = UUID.randomUUID();

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
    when(watchProgressService.getProgress(TEST_USER_ID, contentId))
        .thenReturn(Optional.of(new WatchProgressResponse(contentId, 60, 360)));

    mockMvc
        .perform(get("/api/watch-progress/" + contentId).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contentId").value(contentId.toString()))
        .andExpect(jsonPath("$.progressPercent").value(60))
        .andExpect(jsonPath("$.lastWatchedPosition").value(360));
  }

  @Test
  void shouldReturn204WhenNoProgressExists() throws Exception {
    setupAuthenticatedUser();
    UUID contentId = UUID.randomUUID();

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
    when(watchProgressService.getProgress(TEST_USER_ID, contentId)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/watch-progress/" + contentId).with(csrf()))
        .andExpect(status().isNoContent());
  }
}
