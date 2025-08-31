package com.example.webtoon.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "series")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Series {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type; // "WEBTOON" or "WEBNOVEL"

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    private String coverImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "series_genres", joinColumns = @JoinColumn(name = "series_id"))
    @Column(name = "genre")
    private Set<String> genres;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "series_tags", joinColumns = @JoinColumn(name = "series_id"))
    @Column(name = "tag")
    private Set<String> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "series_authors", joinColumns = @JoinColumn(name = "series_id"))
    @Column(name = "author")
    private Set<String> authors;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
