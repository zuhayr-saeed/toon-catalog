package com.example.webtoon.repo;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserAndSeries(User user, Series series);

    int countBySeries(Series series);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.series.id = :seriesId")
    Double calculateAverageForSeries(UUID seriesId);
}