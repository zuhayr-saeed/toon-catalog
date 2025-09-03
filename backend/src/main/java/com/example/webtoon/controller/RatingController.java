package com.example.webtoon.controller;

import com.example.webtoon.domain.Rating;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.RatingRequest;
import com.example.webtoon.dto.RatingSummary;
import com.example.webtoon.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal(); // ✅ comes from JwtAuthFilter
        Rating rating = ratingService.rate(user.getId(), seriesId, req);
        return ResponseEntity.ok(rating);
    }

    /**
     * GET /api/v1/ratings/{seriesId}/me
     */
    @GetMapping("/{seriesId}/me")
    public ResponseEntity<?> getMyRating(
            @PathVariable UUID seriesId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ratingService.getUserRating(user.getId(), seriesId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * GET /api/v1/ratings/{seriesId}/summary
     */
    @GetMapping("/{seriesId}/summary")
    public ResponseEntity<RatingSummary> getSeriesSummary(
            @PathVariable UUID seriesId
    ) {
        return ResponseEntity.ok(ratingService.getSeriesSummary(seriesId));
    }

    /**
     * DELETE /api/v1/ratings/{seriesId}
     */
    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable UUID seriesId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        boolean deleted = ratingService.deleteRating(user.getId(), seriesId);

        return deleted ? ResponseEntity.noContent().build()
                       : ResponseEntity.notFound().build();
    }
}