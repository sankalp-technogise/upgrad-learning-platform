package com.technogise.upgrad.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technogise.upgrad.backend.config.SecurityConfig;
import com.technogise.upgrad.backend.dto.InterestDTO;
import com.technogise.upgrad.backend.dto.SaveInterestsRequest;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.GlobalExceptionHandler;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.security.JwtAuthenticationFilter;
import com.technogise.upgrad.backend.service.InterestService;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InterestController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class InterestControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private InterestService interestService;

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
            .onboardingCompleted(false)
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
    // Mock the filter to populate SecurityContext with email as principal
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

  // ============ GET /api/interests tests ============

  @Test
  @WithMockUser
  void shouldGetAllInterestsSuccessfully() throws Exception {
    List<InterestDTO> interests =
        List.of(
            new InterestDTO("PYTHON_PROGRAMMING", "Python Programming", "Learn Python", "puzzle"),
            new InterestDTO("DATA_SCIENCE", "Data Science", "Master data analysis", "chart"));

    when(interestService.getAllInterests()).thenReturn(interests);

    mockMvc
        .perform(get("/api/interests"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value("PYTHON_PROGRAMMING"))
        .andExpect(jsonPath("$[0].name").value("Python Programming"))
        .andExpect(jsonPath("$[0].iconName").value("puzzle"))
        .andExpect(jsonPath("$[1].id").value("DATA_SCIENCE"));

    verify(interestService).getAllInterests();
  }

  @Test
  @WithMockUser
  void shouldReturnEmptyListWhenNoInterestsExist() throws Exception {
    when(interestService.getAllInterests()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/interests"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  // ============ POST /api/user/interests tests ============

  @Test
  void shouldSaveUserInterestsSuccessfully() throws Exception {
    setupAuthenticatedUser();
    SaveInterestsRequest request =
        new SaveInterestsRequest(List.of("PYTHON_PROGRAMMING", "DATA_SCIENCE"));

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

    mockMvc
        .perform(
            post("/api/user/interests")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(userRepository).findByEmail(TEST_EMAIL);
    verify(interestService).saveUserInterests(eq(testUser), eq(request.interestNames()));
  }

  @Test
  void shouldReturnBadRequestWhenInterestNamesIsEmpty() throws Exception {
    setupAuthenticatedUser();
    SaveInterestsRequest request = new SaveInterestsRequest(Collections.emptyList());

    mockMvc
        .perform(
            post("/api/user/interests")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(interestService, never()).saveUserInterests(any(), any());
  }

  @Test
  void shouldReturnBadRequestWhenInterestNamesIsNull() throws Exception {
    setupAuthenticatedUser();
    // Manually create JSON with null interestNames
    String jsonContent = "{}";

    mockMvc
        .perform(
            post("/api/user/interests")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
        .andExpect(status().isBadRequest());

    verify(interestService, never()).saveUserInterests(any(), any());
  }

  @Test
  void shouldReturnForbiddenWhenNoCsrfToken() throws Exception {
    setupAuthenticatedUser();
    SaveInterestsRequest request = new SaveInterestsRequest(List.of("PYTHON_PROGRAMMING"));

    // No CSRF token provided
    mockMvc
        .perform(
            post("/api/user/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());

    verify(interestService, never()).saveUserInterests(any(), any());
  }

  @Test
  void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
    setupAuthenticatedUser();
    SaveInterestsRequest request = new SaveInterestsRequest(List.of("PYTHON_PROGRAMMING"));

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

    mockMvc
        .perform(
            post("/api/user/interests")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());

    verify(userRepository).findByEmail(TEST_EMAIL);
    verify(interestService, never()).saveUserInterests(any(), any());
  }

  @Test
  void shouldReturnBadRequestWhenInvalidInterestNames() throws Exception {
    setupAuthenticatedUser();
    SaveInterestsRequest request =
        new SaveInterestsRequest(List.of("PYTHON_PROGRAMMING", "INVALID_INTEREST"));

    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
    Mockito.doThrow(new IllegalArgumentException("Invalid interest names: INVALID_INTEREST"))
        .when(interestService)
        .saveUserInterests(any(), any());

    mockMvc
        .perform(
            post("/api/user/interests")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
