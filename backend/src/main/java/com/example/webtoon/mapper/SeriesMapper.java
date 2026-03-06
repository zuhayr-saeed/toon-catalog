package com.example.webtoon.mapper;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.dto.SeriesDto;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class SeriesMapper {

    public SeriesDto toDto(Series entity) {
        return SeriesDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .synopsis(entity.getSynopsis())
                .coverImageUrl(entity.getCoverImageUrl())
                .genres(copy(entity.getGenres()))
                .tags(copy(entity.getTags()))
                .authors(copy(entity.getAuthors()))
                .createdAt(entity.getCreatedAt())
                .avgRating(entity.getAvgRating())
                .ratingCount(entity.getRatingCount())
                .build();
    }

    public Series fromCreateRequest(SeriesCreateRequest request) {
        return Series.builder()
                .title(request.getTitle())
                .type(normalizeType(request.getType()))
                .synopsis(request.getSynopsis())
                .coverImageUrl(request.getCoverImageUrl())
                .genres(copy(request.getGenres()))
                .tags(copy(request.getTags()))
                .authors(copy(request.getAuthors()))
                .build();
    }

    public void updateSeries(Series series, SeriesCreateRequest request) {
        series.setTitle(request.getTitle());
        series.setType(normalizeType(request.getType()));
        series.setSynopsis(request.getSynopsis());
        series.setCoverImageUrl(request.getCoverImageUrl());
        series.setGenres(copy(request.getGenres()));
        series.setTags(copy(request.getTags()));
        series.setAuthors(copy(request.getAuthors()));
    }

    private Set<String> copy(Set<String> values) {
        if (values == null) {
            return new LinkedHashSet<>();
        }
        LinkedHashSet<String> cleaned = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null) {
                continue;
            }
            String normalized = value.trim();
            if (!normalized.isEmpty()) {
                cleaned.add(normalized);
            }
        }
        return cleaned;
    }

    private String normalizeType(String type) {
        if (type == null || type.isBlank()) {
            return "WEBTOON";
        }
        return type.trim().toUpperCase();
    }
}
