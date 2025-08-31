package com.example.webtoon.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
    name = "episode",
    uniqueConstraints = { @UniqueConstraint(columnNames = {"series_id", "number"}) }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "series_id")
    private Series series;

    private int number;

    private String title;

    private LocalDate releaseDate;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
