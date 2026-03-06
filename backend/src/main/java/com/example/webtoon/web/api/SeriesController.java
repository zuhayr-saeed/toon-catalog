package com.example.webtoon.web.api;

import com.example.webtoon.dto.RatingDto;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.service.RatingService;
import com.example.webtoon.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class SeriesController {
    private final SeriesService seriesService;
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<Page<SeriesDto>> getAllSeries(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        return ResponseEntity.ok(seriesService.getAllSeries(q, genre, tag, sort, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesDto> getSeriesById(@PathVariable UUID id) {
        return ResponseEntity.ok(seriesService.getSeriesById(id));
    }

    @PostMapping
    public ResponseEntity<SeriesDto> createSeries(@Valid @RequestBody SeriesCreateRequest request) {
        SeriesDto created = seriesService.createSeries(request);
        return ResponseEntity.created(URI.create("/api/v1/series/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeriesDto> updateSeries(@PathVariable UUID id,
                                                  @Valid @RequestBody SeriesCreateRequest request) {
        return ResponseEntity.ok(seriesService.updateSeries(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable UUID id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<RatingDto>> getSeriesReviews(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(ratingService.getSeriesReviews(id, pageable));
    }
}
