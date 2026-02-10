package com.technogise.upgrad.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.technogise.upgrad.backend.config.SecurityConfig;
import com.technogise.upgrad.backend.dto.ContentDetailDto;
import com.technogise.upgrad.backend.exception.GlobalExceptionHandler;
import com.technogise.upgrad.backend.exception.ResourceNotFoundException;
import com.technogise.upgrad.backend.security.JwtAuthenticationFilter;
import com.technogise.upgrad.backend.service.ContentService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContentController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ContentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ContentService contentService;

  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  void setUp() throws ServletException, IOException {
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

  @Test
  @org.springframework.security.test.context.support.WithMockUser
  void shouldReturnContent_WhenExists() throws Exception {
    UUID contentId = UUID.randomUUID();
    ContentDetailDto contentDto =
        new ContentDetailDto(
            contentId,
            "Test Content",
            "Test Description",
            "http://example.com/thumb.jpg",
            "http://example.com/video.mp4",
            "Test Category",
            1,
            120,
            LocalDateTime.now());

    when(contentService.getContent(contentId)).thenReturn(contentDto);

    mockMvc
        .perform(get("/api/contents/{id}", contentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(contentId.toString()))
        .andExpect(jsonPath("$.title").value("Test Content"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.thumbnailUrl").value("http://example.com/thumb.jpg"))
        .andExpect(jsonPath("$.videoUrl").value("http://example.com/video.mp4"))
        .andExpect(jsonPath("$.category").value("Test Category"))
        .andExpect(jsonPath("$.episodeNumber").value(1))
        .andExpect(jsonPath("$.durationSeconds").value(120));
  }

  @Test
  @org.springframework.security.test.context.support.WithMockUser
  void shouldReturn404_WhenContentNotFound() throws Exception {
    UUID contentId = UUID.randomUUID();
    when(contentService.getContent(contentId))
        .thenThrow(new ResourceNotFoundException("Content not found"));

    mockMvc.perform(get("/api/contents/{id}", contentId)).andExpect(status().isNotFound());
  }
}
