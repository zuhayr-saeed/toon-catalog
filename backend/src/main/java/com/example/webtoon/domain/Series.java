package com.example.webtoon.domain;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "series", indexes = {
        @Index(name = "idx_series_title", columnList = "title"),
        @Index(name = "idx_series_created_at", columnList = "created_at"),
        @Index(name = "idx_series_avg_rating", columnList = "avg_rating")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Series {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String type = "WEBTOON";

    @Column(length = 4000)
    private String synopsis;

    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "series_genres", joinColumns = @JoinColumn(name = "series_id"))
    @Column(name = "genre", nullable = false)
    @Builder.Default
    private Set<String> genres = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "series_tags", joinColumns = @JoinColumn(name = "series_id"))
    @Column(name = "tag", nullable = false)
    @Builder.Default
    private Set<String> tags = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "series_authors", joinColumns = @JoinColumn(name = "series_id"))
    @Column(name = "author", nullable = false)
    @Builder.Default
    private Set<String> authors = new LinkedHashSet<>();

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Builder.Default
    @Column(name = "avg_rating", nullable = false)
    private Double avgRating = 0.0;

    @Builder.Default
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
