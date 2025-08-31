package com.example.webtoon.service;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.RatingRequest;
import com.example.webtoon.dto.RatingSummary;
import com.example.webtoon.repo.RatingRepository;
import com.example.webtoon.repo.SeriesRepository;
import com.example.webtoon.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;

    /**
     * Submit or update a user's rating for a series
     */
    public Rating rate(UUID userId, UUID seriesId, RatingRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("Series not found"));

        // Validate score range
        if (req.getScore() < 1 || req.getScore() > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }

        // Find existing rating or create new one
        Rating rating = ratingRepository.findByUserIdAndSeriesId(userId, seriesId)
                .orElse(Rating.builder()
                        .user(user)
                        .series(series)
                        .build());

        rating.setScore(req.getScore());
        return ratingRepository.save(rating);
    }

    /**
     * Get a specific user's rating for a series
     */
    public Optional<Rating> getUserRating(UUID userId, UUID seriesId) {
        return ratingRepository.findByUserIdAndSeriesId(userId, seriesId);
    }

    /**
     * Get rating summary (average + count) for a series
     */
    public RatingSummary getSeriesSummary(UUID seriesId) {
        Double avg = ratingRepository.findAverageScoreBySeriesId(seriesId);
        Long count = ratingRepository.countBySeriesId(seriesId);
        return new RatingSummary(avg != null ? avg : 0.0, count);
    }

    /**
     * Delete a user's rating for a series
     */
    public boolean deleteRating(UUID userId, UUID seriesId) {
        Optional<Rating> rating = ratingRepository.findByUserIdAndSeriesId(userId, seriesId);
        if (rating.isPresent()) {
            ratingRepository.delete(rating.get());
            return true;
        }
        return false;
    }
}