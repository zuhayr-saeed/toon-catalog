package com.example.webtoon.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scraper_runs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScraperRun {

    @Id
    @GeneratedValue
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    private Instant finishedAt;

    @Builder.Default
    @Column(nullable = false)
    private int seriesAdded = 0;

    @Builder.Default
    @Column(nullable = false)
    private int seriesUpdated = 0;

    @Builder.Default
    @Column(nullable = false)
    private int seriesFailed = 0;
}