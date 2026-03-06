package com.example.webtoon.service;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.RatingDto;
import com.example.webtoon.dto.RatingRequest;
import com.example.webtoon.dto.RatingSummary;
import com.example.webtoon.repo.RatingRepository;
import com.example.webtoon.repo.SeriesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public RatingDto rate(User user, UUID seriesId, RatingRequest request) {
        Series series = findSeries(seriesId);

        Rating rating = ratingRepository.findByUserAndSeries(user, series)
                .orElseGet(() -> Rating.builder()
                        .user(user)
                        .series(series)
                        .build());

        rating.setScore(request.getScore());
        rating.setReview(normalizeReview(request.getReview()));
        Rating saved = ratingRepository.save(rating);

        updateSeriesAggregate(series);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<RatingDto> getUserRating(User user, UUID seriesId) {
        Series series = findSeries(seriesId);
        return ratingRepository.findByUserAndSeries(user, series)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public RatingSummary getSeriesSummary(UUID seriesId) {
        Series series = findSeries(seriesId);
        double avg = Optional.ofNullable(ratingRepository.calculateAverageForSeries(seriesId)).orElse(0.0);
        int count = ratingRepository.countBySeries(series);
        return new RatingSummary(seriesId, avg, count);
    }

    @Transactional(readOnly = true)
    public Page<RatingDto> getSeriesReviews(UUID seriesId, Pageable pageable) {
        Series series = findSeries(seriesId);
        return ratingRepository.findBySeriesAndReviewIsNotNullAndReviewNot(series, "", pageable)
                .map(this::toDto);
    }

    @Transactional
    public boolean deleteRating(User user, UUID seriesId) {
        Series series = findSeries(seriesId);

        return ratingRepository.findByUserAndSeries(user, series)
                .map(rating -> {
                    ratingRepository.delete(rating);
                    updateSeriesAggregate(series);
                    return true;
                })
                .orElse(false);
    }

    private void updateSeriesAggregate(Series series) {
        Double avg = ratingRepository.calculateAverageForSeries(series.getId());
        int count = ratingRepository.countBySeries(series);

        series.setAvgRating(avg != null ? avg : 0.0);
        series.setRatingCount(count);
        seriesRepository.save(series);
    }

    private Series findSeries(UUID id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Series not found: " + id));
    }

    private String normalizeReview(String review) {
        if (review == null) {
            return null;
        }
        String normalized = review.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private RatingDto toDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .seriesId(rating.getSeries().getId())
                .username(rating.getUser().getUsername())
                .score(rating.getScore())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
}
