package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.technogise.upgrad.backend.constants.Interest;
import com.technogise.upgrad.backend.dto.InterestDTO;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.repository.UserInterestRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

  @Mock private UserInterestRepository userInterestRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private InterestService interestService;

  private User testUser;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    testUser =
        User.builder()
            .id(userId)
            .email("test@example.com")
            .onboardingCompleted(false)
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  void shouldGetAllInterestsFromEnum() {
    // When
    List<InterestDTO> result = interestService.getAllInterests();

    // Then
    assertEquals(Interest.values().length, result.size());
    // Verify no repository call is made
    verifyNoInteractions(userInterestRepository);
  }

  @Test
  void shouldReturnCorrectInterestDetails() {
    // When
    List<InterestDTO> result = interestService.getAllInterests();

    // Then
    InterestDTO pythonInterest =
        result.stream().filter(i -> i.id().equals("PYTHON_PROGRAMMING")).findFirst().orElseThrow();

    assertEquals("Python Programming", pythonInterest.name());
    assertEquals("puzzle", pythonInterest.iconName());
  }

  @Test
  void shouldSaveUserInterestsSuccessfully() {
    // Given
    List<String> interestNames = List.of("PYTHON_PROGRAMMING", "DATA_SCIENCE");

    // When
    interestService.saveUserInterests(testUser, interestNames);

    // Then
    verify(userInterestRepository).deleteByUserId(userId);
    verify(userInterestRepository).saveAll(any());
    verify(userRepository).markOnboardingCompleted(userId);
  }

  @Test
  void shouldThrowExceptionWhenInterestNamesAreInvalid() {
    // Given
    List<String> interestNames = List.of("PYTHON_PROGRAMMING", "INVALID_INTEREST");

    // When / Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> interestService.saveUserInterests(testUser, interestNames));

    assertTrue(exception.getMessage().contains("Invalid interest names"));
    assertTrue(exception.getMessage().contains("INVALID_INTEREST"));
    verify(userInterestRepository, never()).saveAll(any());
  }

  @Test
  void shouldThrowExceptionWhenInterestListIsEmpty() {
    // Given
    List<String> interestNames = List.of();

    // When / Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> interestService.saveUserInterests(testUser, interestNames));

    assertEquals("At least one interest must be selected", exception.getMessage());
    verify(userInterestRepository, never()).saveAll(any());
  }

  @Test
  void shouldMarkOnboardingAsCompletedForNewUser() {
    // Given
    List<String> interestNames = List.of("PYTHON_PROGRAMMING");

    // When
    interestService.saveUserInterests(testUser, interestNames);

    // Then
    verify(userRepository).markOnboardingCompleted(userId);
  }

  @Test
  void shouldNotUpdateOnboardingStatusWhenAlreadyCompleted() {
    // Given
    User completedUser =
        User.builder()
            .id(userId)
            .email("test@example.com")
            .onboardingCompleted(true)
            .createdAt(LocalDateTime.now())
            .build();

    List<String> interestNames = List.of("DATA_SCIENCE");

    // When
    interestService.saveUserInterests(completedUser, interestNames);

    // Then
    verify(userInterestRepository).saveAll(any());
    // markOnboardingCompleted should not be called since onboarding is already
    // completed
    verify(userRepository, never()).markOnboardingCompleted(any());
  }

  @Test
  void shouldDeletePreviousInterestsBeforeSavingNew() {
    // Given
    List<String> interestNames = List.of("CLOUD_COMPUTING");

    // When
    interestService.saveUserInterests(testUser, interestNames);

    // Then
    verify(userInterestRepository).deleteByUserId(userId);
  }

  @Test
  void shouldValidateAllEnumValuesAreValid() {
    // Given - all enum values should be valid
    for (Interest interest : Interest.values()) {
      assertTrue(Interest.isValid(interest.name()));
    }
  }

  @Test
  void shouldRejectInvalidInterestName() {
    // Given
    assertFalse(Interest.isValid("NONEXISTENT"));
    assertFalse(Interest.isValid(null));
    assertFalse(Interest.isValid(""));
  }
}
