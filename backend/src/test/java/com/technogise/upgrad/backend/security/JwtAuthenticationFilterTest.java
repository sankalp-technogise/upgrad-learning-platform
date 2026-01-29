package com.technogise.upgrad.backend.security;

import static org.mockito.Mockito.*;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.technogise.upgrad.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAuthenticateValidToken() throws Exception {
    String token = "valid.token";
    String email = "test@example.com";
    UUID userId = UUID.randomUUID();

    jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", token);
    when(request.getCookies()).thenReturn(new jakarta.servlet.http.Cookie[] {cookie});
    when(jwtService.getEmailFromToken(token)).thenReturn(email);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(jwtService).getEmailFromToken(token);
    verify(filterChain).doFilter(request, response);
    org.junit.jupiter.api.Assertions.assertNotNull(
        SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldContinueChainWhenNoCookie() throws Exception {
    when(request.getCookies()).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(jwtService);
    verify(filterChain).doFilter(request, response);
    org.junit.jupiter.api.Assertions.assertNull(
        SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldContinueChainWhenNoTokenCookie() throws Exception {
    jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("other", "cookie");
    when(request.getCookies()).thenReturn(new jakarta.servlet.http.Cookie[] {cookie});

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verifyNoInteractions(jwtService);
    verify(filterChain).doFilter(request, response);
    org.junit.jupiter.api.Assertions.assertNull(
        SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldHandleInvalidToken() throws Exception {
    String token = "invalid.token";
    jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("token", token);
    when(request.getCookies()).thenReturn(new jakarta.servlet.http.Cookie[] {cookie});
    doThrow(new JWTVerificationException("Invalid token"))
        .when(jwtService)
        .getEmailFromToken(token);

    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    org.junit.jupiter.api.Assertions.assertNull(
        SecurityContextHolder.getContext().getAuthentication());
  }
}
