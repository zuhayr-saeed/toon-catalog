package com.example.webtoon.controller;

import com.example.webtoon.domain.User;
import com.example.webtoon.dto.ListEntryDto;
import com.example.webtoon.service.ListEntryService;
import com.example.webtoon.web.Pageables;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/favorites")
@RequiredArgsConstructor
public class FavoritesController {
    private static final Set<String> FAVORITE_SORTS = Set.of("lastUpdated", "status", "progress", "favorite");

    private final ListEntryService listEntryService;

    @PostMapping("/{seriesId}")
    public ResponseEntity<Void> addFavorite(@PathVariable UUID seriesId,
                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        listEntryService.addFavorite(user, seriesId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID seriesId,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        listEntryService.removeFavorite(user, seriesId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ListEntryDto>> getFavorites(Pageable pageable,
                                                           Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable safePageable = Pageables.bounded(pageable, FAVORITE_SORTS, Sort.by(Sort.Direction.DESC, "lastUpdated"));
        return ResponseEntity.ok(listEntryService.getFavorites(user, safePageable));
    }

    @GetMapping("/{seriesId}/status")
    public ResponseEntity<Boolean> isFavorite(@PathVariable UUID seriesId,
                                              Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(listEntryService.isFavorite(user, seriesId));
    }
}
