package com.example.webtoon.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
