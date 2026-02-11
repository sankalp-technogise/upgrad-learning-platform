package com.technogise.upgrad.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HomepageSectionsDto(
    ContinueWatchingDto continueWatching,
    List<ContentDto> recommended,
    List<ContentDto> exploration) {}
