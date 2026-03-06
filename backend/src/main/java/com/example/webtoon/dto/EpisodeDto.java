package com.example.webtoon.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class EpisodeDto {
    UUID id;
    UUID seriesId;
    Integer number;
    String title;
    LocalDate releaseDate;
}
