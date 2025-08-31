package com.example.webtoon.mapper;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.dto.SeriesCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeriesMapper {
    SeriesDto toDto(Series entity);
    Series toEntity(SeriesDto dto);

    // new mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Series toEntity(SeriesCreateRequest dto);
}
