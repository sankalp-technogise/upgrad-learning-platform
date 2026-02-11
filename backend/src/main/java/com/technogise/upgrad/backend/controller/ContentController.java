package com.technogise.upgrad.backend.controller;

import com.technogise.upgrad.backend.dto.ContentDetailDto;
import com.technogise.upgrad.backend.service.ContentService;
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

  private final ContentService contentService;

  @GetMapping("/{id}")
  public ResponseEntity<ContentDetailDto> getContent(@PathVariable UUID id) {
    return ResponseEntity.ok(contentService.getContent(id));
  }
}
