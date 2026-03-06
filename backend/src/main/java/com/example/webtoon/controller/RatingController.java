package com.example.webtoon.controller;

import com.example.webtoon.domain.User;
import com.example.webtoon.dto.RatingDto;
import com.example.webtoon.dto.RatingRequest;
import com.example.webtoon.dto.RatingSummary;
import com.example.webtoon.service.RatingService;
import jakarta.validation.Valid;
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

    @PostMapping("/{seriesId}")
    public ResponseEntity<RatingDto> rateSeries(@PathVariable UUID seriesId,
                                                @Valid @RequestBody RatingRequest req,
                                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RatingDto rating = ratingService.rate(user, seriesId, req);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/{seriesId}/me")
    public ResponseEntity<RatingDto> getMyRating(@PathVariable UUID seriesId,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ratingService.getUserRating(user, seriesId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{seriesId}/summary")
    public ResponseEntity<RatingSummary> getSeriesSummary(@PathVariable UUID seriesId) {
        return ResponseEntity.ok(ratingService.getSeriesSummary(seriesId));
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID seriesId,
                                             Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean deleted = ratingService.deleteRating(user, seriesId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
