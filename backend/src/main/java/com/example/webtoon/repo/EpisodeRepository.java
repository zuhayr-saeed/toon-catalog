package com.example.webtoon.repo;

import com.example.webtoon.domain.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EpisodeRepository extends JpaRepository<Episode, UUID> {
    Page<Episode> findBySeriesId(UUID seriesId, Pageable pageable);
    List<Episode> findBySeriesIdOrderByNumberAsc(UUID seriesId);
    Optional<Episode> findBySeriesIdAndId(UUID seriesId, UUID episodeId);
    Optional<Episode> findBySeriesIdAndNumber(UUID seriesId, Integer number);
}
