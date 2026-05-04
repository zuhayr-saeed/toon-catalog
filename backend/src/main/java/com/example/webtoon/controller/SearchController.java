package com.example.webtoon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.service.SearchService;
import com.example.webtoon.web.Pageables;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SeriesDto>> search(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        List<SeriesDto> results = searchService.search(
                q,
                genre,
                tag,
                sort,
                Pageables.boundedSearchLimit(limit),
                Pageables.boundedSearchOffset(offset)
        );
        return ResponseEntity.ok(results);
    }
}
