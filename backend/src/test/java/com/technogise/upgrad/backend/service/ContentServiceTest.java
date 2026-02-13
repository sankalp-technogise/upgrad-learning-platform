package com.technogise.upgrad.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.technogise.upgrad.backend.dto.ContentDetailDto;
import com.technogise.upgrad.backend.entity.Content;
import com.technogise.upgrad.backend.exception.ResourceNotFoundException;
import com.technogise.upgrad.backend.repository.ContentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

  @Mock private ContentRepository contentRepository;

  @InjectMocks private ContentService contentService;

  private Content buildContent(UUID id, String category, Integer episodeNumber) {
    return Content.builder()
        .id(id)
        .title("Title " + episodeNumber)
        .description("Description")
        .thumbnailUrl("http://example.com/thumb.jpg")
        .videoUrl("http://example.com/video.mp4")
        .category(category)
        .episodeNumber(episodeNumber)
        .durationSeconds(120)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void shouldReturnNextEpisode_WhenNextExists() {
    UUID currentId = UUID.randomUUID();
    UUID nextId = UUID.randomUUID();
    Content current = buildContent(currentId, "PYTHON", 1);
    Content next = buildContent(nextId, "PYTHON", 2);

    when(contentRepository.findById(currentId)).thenReturn(Optional.of(current));
    when(contentRepository.findFirstByCategoryAndEpisodeNumberGreaterThanOrderByEpisodeNumberAsc(
            "PYTHON", 1))
        .thenReturn(Optional.of(next));

    Optional<ContentDetailDto> result = contentService.getNextEpisode(currentId);

    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(nextId);
    assertThat(result.get().episodeNumber()).isEqualTo(2);
  }

  @Test
  void shouldReturnEmpty_WhenNullEpisodeNumber() {
    UUID contentId = UUID.randomUUID();
    Content content = buildContent(contentId, "PYTHON", null);

    when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));

    Optional<ContentDetailDto> result = contentService.getNextEpisode(contentId);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnEmpty_WhenNoNextEpisode() {
    UUID contentId = UUID.randomUUID();
    Content content = buildContent(contentId, "PYTHON", 5);

    when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
    when(contentRepository.findFirstByCategoryAndEpisodeNumberGreaterThanOrderByEpisodeNumberAsc(
            "PYTHON", 5))
        .thenReturn(Optional.empty());

    Optional<ContentDetailDto> result = contentService.getNextEpisode(contentId);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldThrow_WhenContentNotFound() {
    UUID contentId = UUID.randomUUID();

    when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> contentService.getNextEpisode(contentId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(contentId.toString());
  }

  @Test
  void shouldReturnContent_WhenExists() {
    UUID contentId = UUID.randomUUID();
    Content content = buildContent(contentId, "PYTHON", 1);

    when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));

    ContentDetailDto result = contentService.getContent(contentId);

    assertThat(result.id()).isEqualTo(contentId);
    assertThat(result.title()).isEqualTo("Title 1");
    assertThat(result.category()).isEqualTo("PYTHON");
  }

  @Test
  void shouldThrow_WhenContentNotFoundForGetContent() {
    UUID contentId = UUID.randomUUID();

    when(contentRepository.findById(contentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> contentService.getContent(contentId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(contentId.toString());
  }
}
