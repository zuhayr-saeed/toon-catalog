package com.example.webtoon.controller;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.dto.RatingRequest;
import com.example.webtoon.dto.RatingSummary;
import com.example.webtoon.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    /**
     * POST /api/v1/ratings/{seriesId}
     * Submit or update a rating for a series
     */
    @PostMapping("/{seriesId}")
    public ResponseEntity<Rating> rateSeries(
            @PathVariable UUID seriesId,
            @RequestBody RatingRequest req,
            Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        Rating rating = ratingService.rate(userId, seriesId, req);
        return ResponseEntity.ok(rating);
    }

    /**
     * GET /api/v1/ratings/{seriesId}/me
     * Get the current user's rating for a series
     */
    @GetMapping("/{seriesId}/me")
    public ResponseEntity<?> getMyRating(
            @PathVariable UUID seriesId,
            Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        return ratingService.getUserRating(userId, seriesId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * GET /api/v1/ratings/{seriesId}/summary
     * Get rating summary (average + count) for a series
     */
    @GetMapping("/{seriesId}/summary")
    public ResponseEntity<RatingSummary> getSeriesSummary(
            @PathVariable UUID seriesId
    ) {
        RatingSummary summary = ratingService.getSeriesSummary(seriesId);
        return ResponseEntity.ok(summary);
    }

    /**
     * DELETE /api/v1/ratings/{seriesId}
     * Remove the current user's rating for a series
     */
    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable UUID seriesId,
            Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        boolean deleted = ratingService.deleteRating(userId, seriesId);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}