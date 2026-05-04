package com.example.webtoon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesCreateRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @Pattern(regexp = "WEBTOON|WEBNOVEL", message = "must be WEBTOON or WEBNOVEL")
    private String type;

    @Size(max = 4000)
    private String synopsis;

    @Size(max = 1000)
    private String coverImageUrl;

    @Size(max = 20)
    private Set<@NotBlank @Size(max = 255) String> genres;

    @Size(max = 30)
    private Set<@NotBlank @Size(max = 255) String> tags;

    @Size(max = 20)
    private Set<@NotBlank @Size(max = 255) String> authors;
}
