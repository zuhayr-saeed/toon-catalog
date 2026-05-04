package com.example.webtoon.repo;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserAndSeries(User user, Series series);

    Optional<Rating> findByUserIdAndSeriesId(UUID userId, UUID seriesId);

    int countBySeries(Series series);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.series.id = :seriesId")
    Double calculateAverageForSeries(UUID seriesId);

    Page<Rating> findBySeriesAndReviewIsNotNullAndReviewNot(Series series, String review, Pageable pageable);

    @Query("SELECT r FROM Rating r JOIN FETCH r.series WHERE r.user.id = :userId AND r.series.id IN :seriesIds")
    List<Rating> findAllByUserAndSeriesIds(@Param("userId") UUID userId, @Param("seriesIds") Collection<UUID> seriesIds);
}
