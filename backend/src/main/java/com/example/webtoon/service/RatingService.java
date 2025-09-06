package com.example.webtoon.service;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.RatingRequest;
import com.example.webtoon.dto.RatingSummary;
import com.example.webtoon.repo.RatingRepository;
import com.example.webtoon.repo.SeriesRepository;
import com.example.webtoon.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final SeriesRepository seriesRepository;
    private final UserRepository userRepository;

    @Transactional
    public Rating rate(UUID userId, UUID seriesId, RatingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found"));

        Rating rating = ratingRepository.findByUserAndSeries(user, series)
                .orElseGet(() -> Rating.builder()
                        .user(user)
                        .series(series)
                        .build());

        rating.setScore(request.getScore());
        rating.setReview(request.getReview());
        ratingRepository.save(rating);

        updateSeriesAggregate(series);
        return rating;
    }

    public Optional<Rating> getUserRating(UUID userId, UUID seriesId) {
        User user = userRepository.findById(userId).orElseThrow();
        Series series = seriesRepository.findById(seriesId).orElseThrow();
        return ratingRepository.findByUserAndSeries(user, series);
    }

    public RatingSummary getSeriesSummary(UUID seriesId) {
        Series series = seriesRepository.findById(seriesId).orElseThrow();
        double avg = Optional.ofNullable(ratingRepository.calculateAverageForSeries(seriesId)).orElse(0.0);
        int count = ratingRepository.countBySeries(series);
        return new RatingSummary(seriesId, avg, count);
    }

    @Transactional
    public boolean deleteRating(UUID userId, UUID seriesId) {
        User user = userRepository.findById(userId).orElseThrow();
        Series series = seriesRepository.findById(seriesId).orElseThrow();

        return ratingRepository.findByUserAndSeries(user, series)
                .map(r -> {
                    ratingRepository.delete(r);
                    updateSeriesAggregate(series);
                    return true;
                })
                .orElse(false);
    }

    private void updateSeriesAggregate(Series series) {
        Double newAvg = ratingRepository.calculateAverageForSeries(series.getId());
        Integer newCount = ratingRepository.countBySeries(series);

        series.setAvgRating(newAvg != null ? newAvg : 0.0);
        series.setRatingCount(newCount);
        seriesRepository.save(series);
    }
}