package com.technogise.upgrad.backend.service;

import com.technogise.upgrad.backend.constants.Interest;
import com.technogise.upgrad.backend.dto.InterestDTO;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.entity.UserInterest;
import com.technogise.upgrad.backend.repository.UserInterestRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {
  private final UserInterestRepository userInterestRepository;
  private final UserRepository userRepository;

  /**
   * Returns all available interests from the enum.
   *
   * @return list of available interests as DTOs
   */
  @Transactional(readOnly = true)
  public List<InterestDTO> getAllInterests() {
    return Interest.getAll().stream()
        .map(
            interest ->
                new InterestDTO(
                    interest.name(),
                    interest.getDisplayName(),
                    interest.getDescription(),
                    interest.getIconName()))
        .toList();
  }

  /**
   * Saves user's selected interests.
   *
   * @param user the authenticated user entity
   * @param interestNames list of interest names to save
   * @throws IllegalArgumentException if interest names are empty or invalid
   */
  @Transactional
  public void saveUserInterests(
      @NonNull final User user, @NonNull final List<String> interestNames) {
    if (interestNames == null || interestNames.isEmpty()) {
      throw new IllegalArgumentException("At least one interest must be selected");
    }

    // Validate all interest names
    final List<String> invalidNames =
        interestNames.stream().filter(name -> !Interest.isValid(name)).toList();

    if (!invalidNames.isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid interest names: " + String.join(", ", invalidNames));
    }

    // Delete existing user interests (idempotent operation)
    userInterestRepository.deleteByUserId(user.getId());

    // Create new user-interest relationships
    final List<UserInterest> userInterests =
        interestNames.stream()
            .map(
                interestName ->
                    UserInterest.builder().user(user).interestName(interestName).build())
            .toList();

    userInterestRepository.saveAll(userInterests);

    // Mark onboarding as completed
    if (!user.getOnboardingCompleted()) {
      userRepository.markOnboardingCompleted(user.getId());
    }
  }
}
