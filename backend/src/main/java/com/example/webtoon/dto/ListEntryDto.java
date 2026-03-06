package com.example.webtoon.dto;

import com.example.webtoon.domain.ReadingStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class ListEntryDto {
    UUID id;
    UUID userId;
    String username;
    UUID seriesId;
    String seriesTitle;
    String seriesCoverImageUrl;
    ReadingStatus status;
    Integer progress;
    Boolean favorite;
    Integer userScore;
    Instant lastUpdated;
}
