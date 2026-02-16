package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technogise.upgrad.backend.constants.Feedback;
import com.technogise.upgrad.backend.dto.EpisodeFeedbackRequest;
import com.technogise.upgrad.backend.dto.WatchProgressRequest;
import com.technogise.upgrad.backend.dto.WatchProgressResponse;
import com.technogise.upgrad.backend.entity.Content;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.entity.WatchHistory;
import com.technogise.upgrad.backend.exception.ResourceNotFoundException;
import com.technogise.upgrad.backend.repository.ContentRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import com.technogise.upgrad.backend.repository.WatchHistoryRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatchProgressServiceTest {

  @Mock private WatchHistoryRepository watchHistoryRepository;
  @Mock private ContentRepository contentRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private WatchProgressService watchProgressService;

  private UUID userId;
  private UUID contentId;
  private User testUser;
  private Content testContent;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    contentId = UUID.randomUUID();

    testUser =
        User.builder().id(userId).email("test@example.com").onboardingCompleted(true).build();

    testContent =
        Content.builder()
            .id(contentId)
            .title("Test Video")
            .description("Description")
            .category("PYTHON_PROGRAMMING")
            .episodeNumber(1)
            .durationSeconds(600)
            .build();
  }

  @Test
  void shouldCreateNewProgressWhenNoneExists() {
    WatchProgressRequest request = new WatchProgressRequest(contentId, 45, 270);

    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(contentRepository.findById(contentId)).thenReturn(Optional.of(testContent));
    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.empty());

    watchProgressService.saveProgress(userId, request);

    ArgumentCaptor<WatchHistory> captor = ArgumentCaptor.forClass(WatchHistory.class);
    verify(watchHistoryRepository).save(captor.capture());

    WatchHistory saved = captor.getValue();
    assertEquals(45, saved.getProgressPercent());
    assertEquals(270, saved.getLastWatchedPosition());
    assertEquals(testUser, saved.getUser());
    assertEquals(testContent, saved.getContent());
  }

  @Test
  void shouldUpdateExistingProgress() {
    WatchProgressRequest request = new WatchProgressRequest(contentId, 80, 480);

    WatchHistory existing =
        WatchHistory.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .content(testContent)
            .progressPercent(45)
            .lastWatchedPosition(270)
            .lastWatchedAt(LocalDateTime.now().minusHours(1))
            .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(contentRepository.findById(contentId)).thenReturn(Optional.of(testContent));
    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.of(existing));

    watchProgressService.saveProgress(userId, request);

    verify(watchHistoryRepository).save(existing);
    assertEquals(80, existing.getProgressPercent());
    assertEquals(480, existing.getLastWatchedPosition());
  }

  @Test
  void shouldReturnProgressWhenExists() {
    WatchHistory history =
        WatchHistory.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .content(testContent)
            .progressPercent(60)
            .lastWatchedPosition(360)
            .build();

    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.of(history));

    Optional<WatchProgressResponse> result = watchProgressService.getProgress(userId, contentId);

    assertTrue(result.isPresent());
    assertEquals(contentId, result.get().contentId());
    assertEquals(60, result.get().progressPercent());
    assertEquals(360, result.get().lastWatchedPosition());
  }

  @Test
  void shouldReturnEmptyWhenNoProgress() {
    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.empty());

    Optional<WatchProgressResponse> result = watchProgressService.getProgress(userId, contentId);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldThrowWhenContentNotFound() {
    WatchProgressRequest request = new WatchProgressRequest(contentId, 45, 270);

    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> watchProgressService.saveProgress(userId, request));
  }

  @Test
  void shouldThrowWhenUserNotFound() {
    WatchProgressRequest request = new WatchProgressRequest(contentId, 45, 270);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> watchProgressService.saveProgress(userId, request));
  }

  @Test
  void shouldSaveFeedbackOnExistingWatchHistory() {
    EpisodeFeedbackRequest request = new EpisodeFeedbackRequest(contentId, Feedback.HELPFUL);

    WatchHistory existing =
        WatchHistory.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .content(testContent)
            .progressPercent(100)
            .lastWatchedPosition(600)
            .lastWatchedAt(LocalDateTime.now())
            .build();

    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.of(existing));

    watchProgressService.saveFeedback(userId, request);

    verify(watchHistoryRepository).save(existing);
    assertEquals("HELPFUL", existing.getFeedback());
  }

  @Test
  void shouldUpdateFeedbackWhenUserChangesMind() {
    WatchHistory existing =
        WatchHistory.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .content(testContent)
            .progressPercent(100)
            .lastWatchedPosition(600)
            .lastWatchedAt(LocalDateTime.now())
            .feedback("HELPFUL")
            .build();

    EpisodeFeedbackRequest updatedRequest =
        new EpisodeFeedbackRequest(contentId, Feedback.NOT_HELPFUL);

    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.of(existing));

    watchProgressService.saveFeedback(userId, updatedRequest);

    verify(watchHistoryRepository).save(existing);
    assertEquals("NOT_HELPFUL", existing.getFeedback());
  }

  @Test
  void shouldThrowWhenNoWatchHistoryForFeedback() {
    EpisodeFeedbackRequest request = new EpisodeFeedbackRequest(contentId, Feedback.NOT_HELPFUL);

    when(watchHistoryRepository.findByUserIdAndContentId(userId, contentId))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> watchProgressService.saveFeedback(userId, request));
  }
}
