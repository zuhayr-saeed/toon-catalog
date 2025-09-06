package com.example.webtoon.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "series", indexes = {
        @Index(name = "idx_series_external_id", columnList = "externalId", unique = true),
        @Index(name = "idx_series_genre", columnList = "genre")
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

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(length = 2000)
    private String description;

    /**
     * Unique external identifier from scraper (e.g., site-specific ID).
     */
    @Column(nullable = false, unique = true, updatable = false)
    private String externalId;

    private String genre;

    private String coverImage;

    /**
     * Precomputed rating fields for fast access.
     */
    @Builder.Default  
    @Column(nullable = false)  
    private Double avgRating = 0.0;  
  
    @Builder.Default  
    @Column(nullable = false)  
    private Integer ratingCount = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}