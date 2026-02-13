package com.technogise.upgrad.backend.dto;

import java.util.UUID;

public record WatchProgressResponse(UUID contentId, int progressPercent, int lastWatchedPosition) {}
