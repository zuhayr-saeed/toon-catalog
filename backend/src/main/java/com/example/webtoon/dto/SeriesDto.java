package com.example.webtoon.dto;

import lombok.*;
import java.util.Set;
import java.util.UUID;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesDto {
    private UUID id;
    private String title;
    private String type;   // "WEBTOON" or "WEBNOVEL"
    private String synopsis;
    private String coverImageUrl;
    private Set<String> genres;
    private Set<String> tags;
    private Set<String> authors;
    private Instant createdAt;
}
