package com.example.webtoon.controller;

import com.example.webtoon.domain.User;
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

        User user = (User) authentication.getPrincipal();   // ✅ cast right
        favoritesService.addFavorite(user.getId(), seriesId); // ✅ pass userId
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID seriesId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        favoritesService.removeFavorite(user.getId(), seriesId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<SeriesDto>> getFavorites(
            Pageable pageable,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Page<SeriesDto> favorites = favoritesService.getFavorites(user.getId(), pageable);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{seriesId}/status")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable UUID seriesId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean isFavorite = favoritesService.isFavorite(user.getId(), seriesId);
        return ResponseEntity.ok(isFavorite);
    }
}