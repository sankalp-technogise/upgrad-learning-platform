package com.technogise.upgrad.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.technogise.upgrad.backend.dto.InterestDTO;
import com.technogise.upgrad.backend.entity.Interest;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.repository.InterestRepository;
import com.technogise.upgrad.backend.repository.UserInterestRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

  @Mock private InterestRepository interestRepository;

  @Mock private UserInterestRepository userInterestRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private InterestService interestService;

  private Interest interest1;
  private Interest interest2;
  private User testUser;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    interest1 =
        Interest.builder()
            .id(UUID.randomUUID())
            .name("Java")
            .description("Java programming")
            .iconName("java-icon")
            .displayOrder(1)
            .createdAt(LocalDateTime.now())
            .build();

    interest2 =
        Interest.builder()
            .id(UUID.randomUUID())
            .name("Python")
            .description("Python programming")
            .iconName("python-icon")
            .displayOrder(2)
            .createdAt(LocalDateTime.now())
            .build();

    testUser =
        User.builder()
            .id(userId)
            .email("test@example.com")
            .onboardingCompleted(false)
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  void shouldGetAllInterestsOrderedByDisplayOrder() {
    // Given
    when(interestRepository.findAllByOrderByDisplayOrderAsc())
        .thenReturn(List.of(interest1, interest2));

    // When
    List<InterestDTO> result = interestService.getAllInterests();

    // Then
    assertEquals(2, result.size());
    assertEquals("Java", result.get(0).name());
    assertEquals("Python", result.get(1).name());
    assertEquals(interest1.getId(), result.get(0).id());
    assertEquals(interest2.getId(), result.get(1).id());
    verify(interestRepository).findAllByOrderByDisplayOrderAsc();
  }

  @Test
  void shouldReturnEmptyListWhenNoInterestsExist() {
    // Given
    when(interestRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of());

    // When
    List<InterestDTO> result = interestService.getAllInterests();

    // Then
    assertTrue(result.isEmpty());
    verify(interestRepository).findAllByOrderByDisplayOrderAsc();
  }

  @Test
  void shouldSaveUserInterestsSuccessfully() {
    // Given
    List<UUID> interestIds = List.of(interest1.getId(), interest2.getId());
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(interestRepository.findAllById(interestIds)).thenReturn(List.of(interest1, interest2));

    // When
    interestService.saveUserInterests(
        Objects.requireNonNull(userId), Objects.requireNonNull(interestIds));

    // Then
    verify(userRepository).findById(userId);
    verify(interestRepository).findAllById(interestIds);
    verify(userInterestRepository).deleteByIdUserId(userId);
    verify(userInterestRepository).saveAll(any());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    // Given
    List<UUID> interestIds = List.of(interest1.getId());
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When / Then
    AuthenticationException exception =
        assertThrows(
            AuthenticationException.class,
            () ->
                interestService.saveUserInterests(
                    Objects.requireNonNull(userId), Objects.requireNonNull(interestIds)));

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findById(userId);
    verify(interestRepository, never()).findAllById(any());
  }

  @Test
  void shouldThrowExceptionWhenInterestIdsAreInvalid() {
    // Given
    UUID invalidId = UUID.randomUUID();
    List<UUID> interestIds = List.of(interest1.getId(), invalidId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(interestRepository.findAllById(interestIds)).thenReturn(List.of(interest1)); // Only 1
    // found

    // When / Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                interestService.saveUserInterests(
                    Objects.requireNonNull(userId), Objects.requireNonNull(interestIds)));

    assertEquals("One or more interest IDs are invalid", exception.getMessage());
    verify(userRepository).findById(userId);
    verify(interestRepository).findAllById(interestIds);
    verify(userInterestRepository, never()).saveAll(any());
  }

  @Test
  void shouldMarkOnboardingAsCompletedForNewUser() {
    // Given
    List<UUID> interestIds = List.of(interest1.getId());
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(interestRepository.findAllById(interestIds)).thenReturn(List.of(interest1));

    // When
    interestService.saveUserInterests(
        Objects.requireNonNull(userId), Objects.requireNonNull(interestIds));

    // Then
    verify(userRepository)
        .save(
            argThat(
                user ->
                    user.getId().equals(userId)
                        && user.getEmail().equals("test@example.com")
                        && user.getOnboardingCompleted()));
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

    List<UUID> interestIds = List.of(interest1.getId());
    when(userRepository.findById(userId)).thenReturn(Optional.of(completedUser));
    when(interestRepository.findAllById(interestIds)).thenReturn(List.of(interest1));

    // When
    interestService.saveUserInterests(
        Objects.requireNonNull(userId), Objects.requireNonNull(interestIds));

    // Then
    verify(userInterestRepository).saveAll(any());
    // User save should not be called since onboarding is already completed
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void shouldDeletePreviousInterestsBeforeSavingNew() {
    // Given
    List<UUID> interestIds = List.of(interest1.getId());
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(interestRepository.findAllById(interestIds)).thenReturn(List.of(interest1));

    // When
    interestService.saveUserInterests(
        Objects.requireNonNull(userId), Objects.requireNonNull(interestIds));

    // Then
    verify(userInterestRepository).deleteByIdUserId(userId);
  }

  @Test
  void shouldMapInterestToDTO() {
    // Given
    when(interestRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(interest1));

    // When
    List<InterestDTO> result = interestService.getAllInterests();

    // Then
    InterestDTO dto = result.get(0);
    assertEquals(interest1.getId(), dto.id());
    assertEquals(interest1.getName(), dto.name());
    assertEquals(interest1.getDescription(), dto.description());
    assertEquals(interest1.getIconName(), dto.iconName());
  }
}
