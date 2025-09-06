package com.example.webtoon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SeriesDto>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        List<SeriesDto> results = searchService.search(q, limit, offset);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<SeriesDto>> searchByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        List<SeriesDto> results = searchService.searchByGenre(genre, limit, offset);
        return ResponseEntity.ok(results);
    }
}