package com.example.webtoon.service;

import com.example.webtoon.domain.Episode;
import com.example.webtoon.dto.EpisodeDto;
import com.example.webtoon.repo.EpisodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    @Transactional(readOnly = true)
    public Page<EpisodeDto> getEpisodes(UUID seriesId, Pageable pageable) {
        return episodeRepository.findBySeriesId(seriesId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public EpisodeDto getEpisode(UUID seriesId, UUID episodeId) {
        Episode episode = episodeRepository.findBySeriesIdAndId(seriesId, episodeId)
                .orElseThrow(() -> new EntityNotFoundException("Episode not found"));
        return toDto(episode);
    }

    private EpisodeDto toDto(Episode episode) {
        return EpisodeDto.builder()
                .id(episode.getId())
                .seriesId(episode.getSeries().getId())
                .number(episode.getNumber())
                .title(episode.getTitle())
                .releaseDate(episode.getReleaseDate())
                .build();
    }
}
