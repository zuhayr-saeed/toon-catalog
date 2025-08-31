package com.example.webtoon.service;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.mapper.SeriesMapper;
import com.example.webtoon.repo.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;

    public Page<SeriesDto> getAllSeries(Pageable pageable) {
        return seriesRepository.findAll(pageable).map(seriesMapper::toDto);
    }

    public SeriesDto getSeriesById(UUID id) {
        return seriesRepository.findById(id)
                .map(seriesMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Series not found: " + id));
    }

    public SeriesDto createSeries(SeriesCreateRequest request) {
        Series entity = seriesMapper.toEntity(request);
        Series saved = seriesRepository.save(entity);
        return seriesMapper.toDto(saved);
    }
}
