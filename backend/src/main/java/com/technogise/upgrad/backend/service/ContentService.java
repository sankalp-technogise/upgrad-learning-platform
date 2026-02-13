package com.technogise.upgrad.backend.service;

import com.technogise.upgrad.backend.dto.ContentDetailDto;
import com.technogise.upgrad.backend.entity.Content;
import com.technogise.upgrad.backend.exception.ResourceNotFoundException;
import com.technogise.upgrad.backend.repository.ContentRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

  private final ContentRepository contentRepository;

  public ContentDetailDto getContent(UUID id) {
    return contentRepository
        .findById(id)
        .map(this::toDto)
        .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
  }

  public Optional<ContentDetailDto> getNextEpisode(UUID contentId) {
    final Content current =
        contentRepository
            .findById(contentId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Content not found with id: " + contentId));

    if (current.getEpisodeNumber() == null) {
      return Optional.empty();
    }

    return contentRepository
        .findFirstByCategoryAndEpisodeNumberGreaterThanOrderByEpisodeNumberAsc(
            current.getCategory(), current.getEpisodeNumber())
        .map(this::toDto);
  }

  private ContentDetailDto toDto(Content content) {
    return new ContentDetailDto(
        content.getId(),
        content.getTitle(),
        content.getDescription(),
        content.getThumbnailUrl(),
        content.getVideoUrl(),
        content.getCategory(),
        content.getEpisodeNumber(),
        content.getDurationSeconds(),
        content.getCreatedAt());
  }
}
