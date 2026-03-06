package com.example.webtoon.service;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesCreateRequest;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.mapper.SeriesMapper;
import com.example.webtoon.repo.SeriesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;
    private final SearchService searchService;

    @Transactional(readOnly = true)
    public Page<SeriesDto> getAllSeries(String query, String genre, String tag, String sortBy, Pageable pageable) {
        Pageable effectivePageable = withSort(pageable, sortBy);
        Specification<Series> spec = (root, q, cb) -> cb.conjunction();

        if (query != null && !query.isBlank()) {
            String like = "%" + query.trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> {
                q.distinct(true);
                return cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("synopsis")), like)
                );
            });
        }

        if (genre != null && !genre.isBlank()) {
            String normalized = genre.trim().toLowerCase();
            spec = spec.and((root, q, cb) -> {
                q.distinct(true);
                return cb.equal(cb.lower(root.join("genres")), normalized);
            });
        }

        if (tag != null && !tag.isBlank()) {
            String normalized = tag.trim().toLowerCase();
            spec = spec.and((root, q, cb) -> {
                q.distinct(true);
                return cb.equal(cb.lower(root.join("tags")), normalized);
            });
        }

        return seriesRepository.findAll(spec, effectivePageable).map(seriesMapper::toDto);
    }

    @Transactional(readOnly = true)
    public SeriesDto getSeriesById(UUID id) {
        return seriesMapper.toDto(getSeriesEntityById(id));
    }

    @Transactional(readOnly = true)
    public Series getSeriesEntityById(UUID id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Series not found: " + id));
    }

    @Transactional
    public SeriesDto createSeries(SeriesCreateRequest request) {
        Series entity = seriesMapper.fromCreateRequest(request);
        Series saved = seriesRepository.save(entity);
        searchService.indexSeries(saved);
        return seriesMapper.toDto(saved);
    }

    @Transactional
    public SeriesDto updateSeries(UUID id, SeriesCreateRequest request) {
        Series existing = getSeriesEntityById(id);
        seriesMapper.updateSeries(existing, request);
        Series saved = seriesRepository.save(existing);
        searchService.updateSeries(saved);
        return seriesMapper.toDto(saved);
    }

    @Transactional
    public void deleteSeries(UUID id) {
        Series existing = getSeriesEntityById(id);
        seriesRepository.delete(existing);
        searchService.deleteSeries(id);
    }

    private Pageable withSort(Pageable pageable, String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return pageable;
        }

        Sort sort = switch (sortBy) {
            case "top_rated" -> Sort.by(Sort.Order.desc("avgRating"), Sort.Order.desc("ratingCount"));
            case "popular" -> Sort.by(Sort.Order.desc("ratingCount"), Sort.Order.desc("avgRating"));
            case "newest" -> Sort.by(Sort.Order.desc("createdAt"));
            case "title" -> Sort.by(Sort.Order.asc("title"));
            default -> pageable.getSort().isSorted() ? pageable.getSort() : Sort.unsorted();
        };

        if (sort.isUnsorted()) {
            return pageable;
        }
        return org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
