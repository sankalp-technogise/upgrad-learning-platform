package com.technogise.upgrad.backend.service;

import com.technogise.upgrad.backend.dto.ContentDto;
import com.technogise.upgrad.backend.dto.ContinueWatchingDto;
import com.technogise.upgrad.backend.dto.HomepageSectionsDto;
import com.technogise.upgrad.backend.entity.Content;
import com.technogise.upgrad.backend.entity.UserInterest;
import com.technogise.upgrad.backend.entity.WatchHistory;
import com.technogise.upgrad.backend.repository.ContentRepository;
import com.technogise.upgrad.backend.repository.UserInterestRepository;
import com.technogise.upgrad.backend.repository.WatchHistoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomepageService {

  private static final int COMPLETE_PROGRESS = 100;
  private static final int RECOMMENDED_PAGE_SIZE = 5;

  private final ContentRepository contentRepository;
  private final WatchHistoryRepository watchHistoryRepository;
  private final UserInterestRepository userInterestRepository;

  @Transactional(readOnly = true)
  public HomepageSectionsDto getHomepageSections(@NonNull final UUID userId) {
    final ContinueWatchingDto continueWatching = buildContinueWatching(userId);
    final List<String> userCategories = getUserInterestCategories(userId);
    final List<ContentDto> recommended = buildRecommended(userCategories);
    final List<ContentDto> exploration = buildExploration(userCategories);

    return new HomepageSectionsDto(continueWatching, recommended, exploration);
  }

  private ContinueWatchingDto buildContinueWatching(final UUID userId) {
    return watchHistoryRepository
        .findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
            userId, COMPLETE_PROGRESS)
        .map(this::toDto)
        .orElse(null);
  }

  private List<String> getUserInterestCategories(final UUID userId) {
    return userInterestRepository.findByUserId(userId).stream()
        .map(UserInterest::getInterestName)
        .toList();
  }

  private List<ContentDto> buildRecommended(final List<String> userCategories) {
    if (userCategories.isEmpty()) {
      return List.of();
    }
    return contentRepository
        .findByCategoryIn(userCategories, PageRequest.of(0, RECOMMENDED_PAGE_SIZE))
        .stream()
        .map(this::toDto)
        .toList();
  }

  private List<ContentDto> buildExploration(final List<String> userCategories) {
    if (userCategories.isEmpty()) {
      return contentRepository.findAll(PageRequest.of(0, RECOMMENDED_PAGE_SIZE)).stream()
          .map(this::toDto)
          .toList();
    }
    return contentRepository
        .findByCategoryNotIn(userCategories, PageRequest.of(0, RECOMMENDED_PAGE_SIZE))
        .stream()
        .map(this::toDto)
        .toList();
  }

  private ContinueWatchingDto toDto(final WatchHistory watchHistory) {
    final Content content = watchHistory.getContent();
    return new ContinueWatchingDto(
        content.getId(),
        content.getTitle(),
        content.getDescription(),
        content.getThumbnailUrl(),
        watchHistory.getProgressPercent(),
        content.getCategory(),
        content.getEpisodeNumber(),
        watchHistory.getLastWatchedPosition());
  }

  private ContentDto toDto(final Content content) {
    return new ContentDto(
        content.getId(),
        content.getTitle(),
        content.getDescription(),
        content.getThumbnailUrl(),
        content.getCategory());
  }
}
