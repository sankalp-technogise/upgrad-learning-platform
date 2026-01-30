package com.technogise.upgrad.backend.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technogise.upgrad.backend.dto.AuthResponse;
import com.technogise.upgrad.backend.dto.LoginRequest;
import com.technogise.upgrad.backend.dto.OtpRequest;
import com.technogise.upgrad.backend.dto.UserDto;
import com.technogise.upgrad.backend.exception.GlobalExceptionHandler;
import com.technogise.upgrad.backend.service.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, com.technogise.upgrad.backend.config.SecurityConfig.class})
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;

  @MockitoBean
  private com.technogise.upgrad.backend.security.JwtAuthenticationFilter jwtAuthenticationFilter;

  @org.junit.jupiter.api.BeforeEach
  void setUp() throws jakarta.servlet.ServletException, java.io.IOException {
    org.mockito.Mockito.doAnswer(
            invocation -> {
              jakarta.servlet.FilterChain chain = invocation.getArgument(2);
              chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
              return null;
            })
        .when(jwtAuthenticationFilter)
        .doFilter(
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.any());
  }

  @Test
  @SuppressWarnings("null")
  void shouldRequestOtpSuccessfully() throws Exception {

    final OtpRequest request = new OtpRequest("test@example.com");

    mockMvc
        .perform(
            post("/api/auth/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    verify(authService).generateOtp(request.email());
  }

  @Test
  @SuppressWarnings("null") // MediaType.APPLICATION_JSON, ObjectMapper.writeValueAsString
  void shouldReturnBadRequestWhenRequestOtpWithInvalidEmail() throws Exception {
    final OtpRequest request = new OtpRequest("invalid");

    mockMvc
        .perform(
            post("/api/auth/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SuppressWarnings("null") // MediaType.APPLICATION_JSON, ObjectMapper.writeValueAsString
  void shouldLoginSuccessfully() throws Exception {
    final LoginRequest request = new LoginRequest("test@example.com", "123456");
    final UUID userId = UUID.randomUUID();
    final AuthResponse authResponse =
        new AuthResponse("jwt-token", new UserDto(userId, "test@example.com"));

    when(authService.login(anyString(), anyString())).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie()
                .value("token", "jwt-token"))
        .andExpect(jsonPath("$.token").value("jwt-token"))
        .andExpect(jsonPath("$.user.email").value("test@example.com"));

    verify(authService).login(request.email(), request.otp());
  }

  @Test
  @SuppressWarnings("null") // MediaType.APPLICATION_JSON, ObjectMapper.writeValueAsString
  void shouldReturnBadRequestWhenLoginWithInvalidEmail() throws Exception {
    final LoginRequest request = new LoginRequest("invalid", "123456");

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldLogoutSuccessfully() throws Exception {
    mockMvc
        .perform(post("/api/auth/logout"))
        .andExpect(status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie()
                .value("token", ""))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie()
                .maxAge("token", 0));
  }

  @Test
  void shouldReturnCurrentUser() throws Exception {
    UUID userId = UUID.randomUUID();
    String email = "test@example.com";
    UserDto userDto = new UserDto(userId, email);

    when(authService.getUser(email)).thenReturn(userDto);

    // Mock the filter to populate SecurityContext with email as principal
    org.mockito.Mockito.doAnswer(
            invocation -> {
              org.springframework.security.core.Authentication auth =
                  new org.springframework.security.authentication
                      .UsernamePasswordAuthenticationToken(
                      email, null, java.util.Collections.emptyList());
              org.springframework.security.core.context.SecurityContextHolder.getContext()
                  .setAuthentication(auth);
              jakarta.servlet.FilterChain chain = invocation.getArgument(2);
              chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
              return null;
            })
        .when(jwtAuthenticationFilter)
        .doFilter(
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.any());

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(email));
  }
}
