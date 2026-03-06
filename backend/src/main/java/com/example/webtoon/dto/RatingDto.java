package com.example.webtoon.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class RatingDto {
    UUID id;
    UUID seriesId;
    String username;
    Integer score;
    String review;
    Instant createdAt;
    Instant updatedAt;
}
