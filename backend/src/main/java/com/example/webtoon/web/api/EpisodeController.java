package com.example.webtoon.web.api;

import com.example.webtoon.dto.EpisodeDto;
import com.example.webtoon.service.EpisodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/series/{seriesId}/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @GetMapping
    public ResponseEntity<Page<EpisodeDto>> getEpisodes(@PathVariable UUID seriesId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "number"));
        return ResponseEntity.ok(episodeService.getEpisodes(seriesId, pageable));
    }

    @GetMapping("/{episodeId}")
    public ResponseEntity<EpisodeDto> getEpisode(@PathVariable UUID seriesId,
                                                 @PathVariable UUID episodeId) {
        return ResponseEntity.ok(episodeService.getEpisode(seriesId, episodeId));
    }
}
