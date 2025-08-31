package com.example.webtoon.repo;

import com.example.webtoon.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserIdAndSeriesId(UUID userId, UUID seriesId);

    // averageRating + count per series
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.series.id = :seriesId")
    Double findAverageScoreBySeriesId(UUID seriesId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.series.id = :seriesId")
    Long countBySeriesId(UUID seriesId);
}