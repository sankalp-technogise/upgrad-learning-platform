package com.technogise.upgrad.backend.dto;

import java.util.UUID;

public record ContentDto(
    UUID id, String title, String description, String thumbnailUrl, String category) {}
