package com.technogise.upgrad.backend.service;

import com.technogise.upgrad.backend.dto.InterestDTO;
import com.technogise.upgrad.backend.entity.Interest;
import com.technogise.upgrad.backend.entity.User;
import com.technogise.upgrad.backend.entity.UserInterest;
import com.technogise.upgrad.backend.entity.UserInterestId;
import com.technogise.upgrad.backend.exception.AuthenticationException;
import com.technogise.upgrad.backend.repository.InterestRepository;
import com.technogise.upgrad.backend.repository.UserInterestRepository;
import com.technogise.upgrad.backend.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {
  private final InterestRepository interestRepository;
  private final UserInterestRepository userInterestRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public List<InterestDTO> getAllInterests() {
    return interestRepository.findAllByOrderByDisplayOrderAsc().stream()
        .map(
            interest ->
                new InterestDTO(
                    interest.getId(),
                    interest.getName(),
                    interest.getDescription(),
                    interest.getIconName()))
        .toList();
  }

  @Transactional
  public void saveUserInterests(@NonNull final UUID userId, @NonNull final List<UUID> interestIds) {
    // Validate user exists
    final User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new AuthenticationException("User not found"));

    // Validate interest IDs exist
    final List<Interest> interests = interestRepository.findAllById(interestIds);
    if (interests.size() != interestIds.size()) {
      throw new IllegalArgumentException("One or more interest IDs are invalid");
    }

    // Delete existing user interests (idempotent operation)
    userInterestRepository.deleteByIdUserId(userId);

    // Create new user-interest relationships
    final List<UserInterest> userInterests =
        interests.stream()
            .map(
                interest ->
                    UserInterest.builder()
                        .id(new UserInterestId(userId, interest.getId()))
                        .user(user)
                        .interest(interest)
                        .build())
            .toList();

    userInterestRepository.saveAll(userInterests);

    // Mark onboarding as completed
    if (!user.getOnboardingCompleted()) {
      final User updatedUser =
          User.builder()
              .id(user.getId())
              .email(user.getEmail())
              .onboardingCompleted(true)
              .createdAt(user.getCreatedAt())
              .build();
      userRepository.save(updatedUser);
    }
  }
}
