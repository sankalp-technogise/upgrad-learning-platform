package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technogise.upgrad.backend.dto.ContentDto;
import com.technogise.upgrad.backend.dto.ContinueWatchingDto;
import com.technogise.upgrad.backend.dto.HomepageSectionsDto;
import com.technogise.upgrad.backend.entity.Content;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.entity.UserInterest;
import com.technogise.upgrad.backend.entity.WatchHistory;
import com.technogise.upgrad.backend.repository.ContentRepository;
import com.technogise.upgrad.backend.repository.UserInterestRepository;
import com.technogise.upgrad.backend.repository.WatchHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class HomepageServiceTest {

  @Mock private ContentRepository contentRepository;
  @Mock private WatchHistoryRepository watchHistoryRepository;
  @Mock private UserInterestRepository userInterestRepository;

  @InjectMocks private HomepageService homepageService;

  private UUID userId;
  private User testUser;
  private Content pythonContent;
  private Content designContent;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    testUser =
        User.builder()
            .id(userId)
            .email("test@example.com")
            .onboardingCompleted(true)
            .createdAt(LocalDateTime.now())
            .build();

    pythonContent =
        Content.builder()
            .id(UUID.randomUUID())
            .title("Advanced Python")
            .description("Python course")
            .thumbnailUrl("https://example.com/python.jpg")
            .category("PYTHON_PROGRAMMING")
            .build();

    designContent =
        Content.builder()
            .id(UUID.randomUUID())
            .title("UI/UX Design")
            .description("Design course")
            .thumbnailUrl("https://example.com/design.jpg")
            .category("UI_UX_DESIGN")
            .build();
  }

  @Test
  void shouldReturnContinueWatchingWhenIncompleteVideoExists() {
    WatchHistory watchHistory =
        WatchHistory.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .content(pythonContent)
            .progressPercent(45)
            .lastWatchedAt(LocalDateTime.now())
            .build();

    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.of(watchHistory));
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of());
    when(contentRepository.findAll(any(Pageable.class)))
        .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    assertNotNull(result.continueWatching());
    assertEquals(pythonContent.getId(), result.continueWatching().contentId());
    assertEquals(45, result.continueWatching().progressPercent());
  }

  @Test
  void shouldReturnNullContinueWatchingWhenNoIncompleteVideo() {
    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.empty());
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of());
    when(contentRepository.findAll(any(Pageable.class)))
        .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    assertNull(result.continueWatching());
  }

  @Test
  void shouldReturnInterestBasedRecommendations() {
    UserInterest interest =
        UserInterest.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .interestName("PYTHON_PROGRAMMING")
            .build();

    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.empty());
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of(interest));
    when(contentRepository.findByCategoryIn(eq(List.of("PYTHON_PROGRAMMING")), any(Pageable.class)))
        .thenReturn(List.of(pythonContent));
    when(contentRepository.findByCategoryNotIn(
            eq(List.of("PYTHON_PROGRAMMING")), any(Pageable.class)))
        .thenReturn(List.of(designContent));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    assertEquals(1, result.recommended().size());
    assertEquals("Advanced Python", result.recommended().get(0).title());
  }

  @Test
  void shouldReturnExplorationContentOutsideUserInterests() {
    UserInterest interest =
        UserInterest.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .interestName("PYTHON_PROGRAMMING")
            .build();

    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.empty());
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of(interest));
    when(contentRepository.findByCategoryIn(eq(List.of("PYTHON_PROGRAMMING")), any(Pageable.class)))
        .thenReturn(List.of(pythonContent));
    when(contentRepository.findByCategoryNotIn(
            eq(List.of("PYTHON_PROGRAMMING")), any(Pageable.class)))
        .thenReturn(List.of(designContent));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    assertEquals(1, result.exploration().size());
    assertEquals("UI/UX Design", result.exploration().get(0).title());
  }

  @Test
  void shouldReturnAllContentAsExplorationWhenNoInterests() {
    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.empty());
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of());
    when(contentRepository.findAll(any(Pageable.class)))
        .thenReturn(
            new org.springframework.data.domain.PageImpl<>(List.of(pythonContent, designContent)));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    assertTrue(result.recommended().isEmpty());
    assertEquals(2, result.exploration().size());
  }

  @Test
  void shouldReturnInterestBasedBeforeExploration() {
    UserInterest interest =
        UserInterest.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .interestName("PYTHON_PROGRAMMING")
            .build();

    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.empty());
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of(interest));
    when(contentRepository.findByCategoryIn(eq(List.of("PYTHON_PROGRAMMING")), any(Pageable.class)))
        .thenReturn(List.of(pythonContent));
    when(contentRepository.findByCategoryNotIn(
            eq(List.of("PYTHON_PROGRAMMING")), any(Pageable.class)))
        .thenReturn(List.of(designContent));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    assertNotNull(result.recommended());
    assertNotNull(result.exploration());
    assertFalse(result.recommended().isEmpty());
    assertFalse(result.exploration().isEmpty());

    List<ContentDto> allContent = new java.util.ArrayList<>(result.recommended());
    allContent.addAll(result.exploration());
    assertEquals("Advanced Python", allContent.get(0).title());
    assertEquals("UI/UX Design", allContent.get(1).title());
  }

  @Test
  void shouldMapContinueWatchingDtoCorrectly() {
    WatchHistory watchHistory =
        WatchHistory.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .content(pythonContent)
            .progressPercent(72)
            .lastWatchedAt(LocalDateTime.now())
            .build();

    when(watchHistoryRepository.findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, 100))
        .thenReturn(Optional.of(watchHistory));
    when(userInterestRepository.findByUserId(userId)).thenReturn(List.of());
    when(contentRepository.findAll(any(Pageable.class)))
        .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

    HomepageSectionsDto result = homepageService.getHomepageSections(userId);

    ContinueWatchingDto cw = result.continueWatching();
    assertNotNull(cw);
    assertEquals(pythonContent.getId(), cw.contentId());
    assertEquals("Advanced Python", cw.title());
    assertEquals("Python course", cw.description());
    assertEquals("https://example.com/python.jpg", cw.thumbnailUrl());
    assertEquals(72, cw.progressPercent());
  }
}
