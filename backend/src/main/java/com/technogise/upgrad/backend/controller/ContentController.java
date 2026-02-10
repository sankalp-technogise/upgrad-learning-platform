package com.technogise.upgrad.backend.controller;

import com.technogise.upgrad.backend.dto.ContentDetailDto;
import com.technogise.upgrad.backend.entity.Content;
import com.technogise.upgrad.backend.exception.ResourceNotFoundException;
import com.technogise.upgrad.backend.repository.ContentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

  private final ContentRepository contentRepository;

  @GetMapping("/{id}")
  public ResponseEntity<ContentDetailDto> getContent(@PathVariable UUID id) {
    return contentRepository
        .findById(id)
        .map(this::toDto)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
  }

  private ContentDetailDto toDto(Content content) {
    return new ContentDetailDto(
        content.getId(),
        content.getTitle(),
        content.getDescription(),
        content.getThumbnailUrl(),
        content.getVideoUrl(),
        content.getCategory(),
        content.getCreatedAt());
  }
}
