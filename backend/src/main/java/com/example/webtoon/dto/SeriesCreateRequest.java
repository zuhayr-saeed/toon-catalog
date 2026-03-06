package com.example.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesCreateRequest {
    @NotBlank
    private String title;
    private String type;
    private String synopsis;
    private String coverImageUrl;
    private Set<String> genres;
    private Set<String> tags;
    private Set<String> authors;
}
