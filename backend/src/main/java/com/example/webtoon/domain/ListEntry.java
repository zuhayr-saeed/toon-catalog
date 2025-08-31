package com.example.webtoon.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.Instant;

@Entity
@Table(
    name = "list_entry",
    uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "series_id"}) }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListEntry {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "series_id")
    private Series series;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    @Builder.Default
    private int progress = 0;

    @Builder.Default
    private boolean favorite = false;

    @Builder.Default
    private Instant lastUpdated = Instant.now();

    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = Instant.now();
    }
}
