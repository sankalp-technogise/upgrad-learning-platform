package com.technogise.upgrad.backend.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WatchProgressService {

  private final WatchHistoryRepository watchHistoryRepository;
  private final ContentRepository contentRepository;
  private final UserRepository userRepository;

  @Transactional
  public void saveProgress(
      @NonNull final UUID userId, @NonNull final WatchProgressRequest request) {

    final User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    final Content content =
        contentRepository
            .findById(request.contentId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Content not found: " + request.contentId()));

    final Optional<WatchHistory> existing =
        watchHistoryRepository.findByUserIdAndContentId(userId, request.contentId());

    if (existing.isPresent()) {
      final WatchHistory history = existing.get();
      history.setProgressPercent(request.progressPercent());
      history.setLastWatchedPosition(request.lastWatchedPosition());
      history.setLastWatchedAt(LocalDateTime.now());
      watchHistoryRepository.save(history);
    } else {
      final WatchHistory history =
          WatchHistory.builder()
              .user(user)
              .content(content)
              .progressPercent(request.progressPercent())
              .lastWatchedPosition(request.lastWatchedPosition())
              .lastWatchedAt(LocalDateTime.now())
              .build();
      watchHistoryRepository.save(history);
    }
  }

  @Transactional(readOnly = true)
  public Optional<WatchProgressResponse> getProgress(
      @NonNull final UUID userId, @NonNull final UUID contentId) {
    return watchHistoryRepository
        .findByUserIdAndContentId(userId, contentId)
        .map(
            wh ->
                new WatchProgressResponse(
                    wh.getContent().getId(), wh.getProgressPercent(), wh.getLastWatchedPosition()));
  }

  @Transactional
  public void saveFeedback(
      @NonNull final UUID userId, @NonNull final EpisodeFeedbackRequest request) {
    final WatchHistory history =
        watchHistoryRepository
            .findByUserIdAndContentId(userId, request.contentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No watch history found for content: " + request.contentId()));

    history.setFeedback(request.feedback());
    watchHistoryRepository.save(history);
  }
}
