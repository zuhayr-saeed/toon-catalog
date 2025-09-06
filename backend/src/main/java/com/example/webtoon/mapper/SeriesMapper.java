package com.example.webtoon.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.dto.SeriesDto;

@Mapper(componentModel = "spring")
public interface SeriesMapper {

    // Custom mappers for genre <-> genres
    default Set<String> mapGenre(String genre) {
        if (genre == null || genre.isBlank()) return Collections.emptySet();
        return Arrays.stream(genre.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    default String mapGenres(Set<String> genres) {
        if (genres == null || genres.isEmpty()) return null;
        return String.join(",", genres);
    }

    // Entity -> DTO
    @Mapping(target = "genres", expression = "java(mapGenre(entity.getGenre()))")
    @Mapping(target = "coverImageUrl", source = "coverImage")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "synopsis", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "authors", ignore = true)
    SeriesDto toDto(Series entity);

    // DTO -> Entity
    @Mapping(target = "genre", expression = "java(mapGenres(dto.getGenres()))")
    @Mapping(target = "coverImage", source = "coverImageUrl")
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "avgRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "description", source = "synopsis", ignore = true) // if mismatch
    @Mapping(target = "author", ignore = true) // fix later
    Series toEntity(SeriesDto dto);

    // CreateRequest DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "avgRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    Series toEntity(SeriesCreateRequest dto);
}