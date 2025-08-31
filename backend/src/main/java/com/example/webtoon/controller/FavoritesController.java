package com.example.webtoon.controller;

import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    @PostMapping("/{seriesId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable UUID seriesId,
            Authentication authentication) {
        String username = authentication.getName();
        favoritesService.addFavorite(username, seriesId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID seriesId,
            Authentication authentication) {
        String username = authentication.getName();
        favoritesService.removeFavorite(username, seriesId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<SeriesDto>> getFavorites(
            Pageable pageable,
            Authentication authentication) {
        String username = authentication.getName();
        Page<SeriesDto> favorites = favoritesService.getFavorites(username, pageable);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{seriesId}/status")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable UUID seriesId,
            Authentication authentication) {
        String username = authentication.getName();
        boolean isFavorite = favoritesService.isFavorite(username, seriesId);
        return ResponseEntity.ok(isFavorite);
    }
}
