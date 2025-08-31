package com.example.webtoon.web.api;

import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.service.SeriesService;
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

    @GetMapping
    public ResponseEntity<Page<SeriesDto>> getAllSeries(Pageable pageable) {
        return ResponseEntity.ok(seriesService.getAllSeries(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesDto> getSeriesById(@PathVariable UUID id) {
        return ResponseEntity.ok(seriesService.getSeriesById(id));
    }

    @PostMapping
    public ResponseEntity<SeriesDto> createSeries(@RequestBody SeriesCreateRequest request) {
        SeriesDto created = seriesService.createSeries(request);
        return ResponseEntity
                .created(URI.create("/api/v1/series/" + created.getId()))
                .body(created);
    }
}
