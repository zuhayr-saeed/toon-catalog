package com.example.webtoon.web.api;

import com.example.webtoon.dto.RatingDto;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.service.RatingService;
import com.example.webtoon.service.SeriesService;
import com.example.webtoon.web.Pageables;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class SeriesController {
    private static final Set<String> SERIES_SORTS = Set.of("title", "avgRating", "ratingCount", "createdAt");
    private static final Set<String> SERIES_SORT_TOKENS = Set.of("top_rated", "popular", "newest", "title");
    private static final Set<String> REVIEW_SORTS = Set.of("createdAt", "updatedAt", "score");

    private final SeriesService seriesService;
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<Page<SeriesDto>> getAllSeries(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        String sortToken = sort == null ? null : sort.trim();
        Pageable safePageable = sanitizeSeriesPageable(sortToken, pageable);
        return ResponseEntity.ok(seriesService.getAllSeries(q, genre, tag, sortToken, safePageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesDto> getSeriesById(@PathVariable UUID id) {
        return ResponseEntity.ok(seriesService.getSeriesById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeriesDto> createSeries(@Valid @RequestBody SeriesCreateRequest request) {
        SeriesDto created = seriesService.createSeries(request);
        return ResponseEntity.created(URI.create("/api/v1/series/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeriesDto> updateSeries(@PathVariable UUID id,
                                                  @Valid @RequestBody SeriesCreateRequest request) {
        return ResponseEntity.ok(seriesService.updateSeries(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSeries(@PathVariable UUID id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<RatingDto>> getSeriesReviews(@PathVariable UUID id, Pageable pageable) {
        Pageable safePageable = Pageables.bounded(pageable, REVIEW_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ratingService.getSeriesReviews(id, safePageable));
    }

    private Pageable sanitizeSeriesPageable(String sort, Pageable pageable) {
        if (sort != null && !sort.isBlank()) {
            if (!SERIES_SORT_TOKENS.contains(sort)) {
                throw new IllegalArgumentException("Unsupported series sort: " + sort);
            }
            int page = pageable == null || pageable.isUnpaged() ? 0 : pageable.getPageNumber();
            int size = pageable == null || pageable.isUnpaged() ? Pageables.DEFAULT_SIZE : pageable.getPageSize();
            return Pageables.bounded(page, size, Sort.unsorted());
        }
        return Pageables.bounded(pageable, SERIES_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
