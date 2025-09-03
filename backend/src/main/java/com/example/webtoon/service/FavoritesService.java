package com.example.webtoon.service;

import com.example.webtoon.domain.ListEntry;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.mapper.SeriesMapper;
import com.example.webtoon.repo.ListEntryRepository;
import com.example.webtoon.repo.SeriesRepository;
import com.example.webtoon.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoritesService {

    private final ListEntryRepository listEntryRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;

    public void addFavorite(UUID userId, UUID seriesId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Series series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new IllegalArgumentException("Series not found"));

        if (listEntryRepository.existsByUserAndSeries(user, series)) return;

        ListEntry listEntry = ListEntry.builder()
            .user(user)      // ✅ fix
            .series(series)
            .favorite(true)
            .build();

        listEntryRepository.save(listEntry);
    }

    public void removeFavorite(UUID userId, UUID seriesId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Series series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new IllegalArgumentException("Series not found"));

        listEntryRepository.deleteByUserAndSeries(user, series);
    }

    @Transactional(readOnly = true)
    public Page<SeriesDto> getFavorites(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return listEntryRepository.findByUser(user, pageable)
            .map(entry -> seriesMapper.toDto(entry.getSeries()));
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(UUID userId, UUID seriesId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Series series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new IllegalArgumentException("Series not found"));

        return listEntryRepository.existsByUserAndSeries(user, series);
    }
}