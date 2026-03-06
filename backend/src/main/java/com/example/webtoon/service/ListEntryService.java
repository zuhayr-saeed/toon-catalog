package com.example.webtoon.service;

import com.example.webtoon.domain.ListEntry;
import com.example.webtoon.domain.ReadingStatus;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.ListEntryDto;
import com.example.webtoon.dto.ListEntryUpsertRequest;
import com.example.webtoon.repo.ListEntryRepository;
import com.example.webtoon.repo.RatingRepository;
import com.example.webtoon.repo.SeriesRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListEntryService {

    private final ListEntryRepository listEntryRepository;
    private final SeriesRepository seriesRepository;
    private final RatingRepository ratingRepository;

    @Transactional(readOnly = true)
    public Page<ListEntryDto> getMyList(User user, ReadingStatus status, Boolean favorite, Pageable pageable) {
        return filterEntries(user, status, favorite, pageable)
                .map(entry -> toDto(entry, user));
    }

    @Transactional(readOnly = true)
    public Optional<ListEntryDto> getMyListEntry(User user, UUID seriesId) {
        Series series = findSeries(seriesId);
        return listEntryRepository.findByUserAndSeries(user, series)
                .map(entry -> toDto(entry, user));
    }

    @Transactional
    public ListEntryDto upsert(User user, UUID seriesId, ListEntryUpsertRequest request) {
        Series series = findSeries(seriesId);

        ListEntry entry = listEntryRepository.findByUserAndSeries(user, series)
                .orElseGet(() -> ListEntry.builder()
                        .user(user)
                        .series(series)
                        .status(ReadingStatus.PLAN_TO_READ)
                        .progress(0)
                        .favorite(false)
                        .build());

        if (request.getStatus() != null) {
            entry.setStatus(request.getStatus());
        }
        if (request.getProgress() != null) {
            entry.setProgress(request.getProgress());
        }
        if (request.getFavorite() != null) {
            entry.setFavorite(request.getFavorite());
        }

        ListEntry saved = listEntryRepository.save(entry);
        return toDto(saved, user);
    }

    @Transactional
    public void delete(User user, UUID seriesId) {
        Series series = findSeries(seriesId);
        listEntryRepository.deleteByUserAndSeries(user, series);
    }

    @Transactional
    public void addFavorite(User user, UUID seriesId) {
        Series series = findSeries(seriesId);
        ListEntry entry = listEntryRepository.findByUserAndSeries(user, series)
                .orElseGet(() -> ListEntry.builder()
                        .user(user)
                        .series(series)
                        .status(ReadingStatus.PLAN_TO_READ)
                        .progress(0)
                        .favorite(false)
                        .build());

        entry.setFavorite(true);
        listEntryRepository.save(entry);
    }

    @Transactional
    public void removeFavorite(User user, UUID seriesId) {
        Series series = findSeries(seriesId);
        listEntryRepository.findByUserAndSeries(user, series).ifPresent(entry -> {
            entry.setFavorite(false);
            ListEntry saved = listEntryRepository.save(entry);

            boolean shouldDelete = saved.getStatus() == ReadingStatus.PLAN_TO_READ
                    && saved.getProgress() == 0
                    && ratingRepository.findByUserAndSeries(user, series).isEmpty();

            if (shouldDelete) {
                listEntryRepository.delete(saved);
            }
        });
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(User user, UUID seriesId) {
        Series series = findSeries(seriesId);
        return listEntryRepository.findByUserAndSeries(user, series)
                .map(ListEntry::isFavorite)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<ListEntryDto> getFavorites(User user, Pageable pageable) {
        return listEntryRepository.findByUserAndFavoriteTrue(user, pageable)
                .map(entry -> toDto(entry, user));
    }

    private Page<ListEntry> filterEntries(User user, ReadingStatus status, Boolean favorite, Pageable pageable) {
        if (status != null && favorite != null) {
            return listEntryRepository.findByUserAndStatusAndFavorite(user, status, favorite, pageable);
        }
        if (status != null) {
            return listEntryRepository.findByUserAndStatus(user, status, pageable);
        }
        if (favorite != null) {
            return listEntryRepository.findByUserAndFavorite(user, favorite, pageable);
        }
        return listEntryRepository.findByUser(user, pageable);
    }

    private ListEntryDto toDto(ListEntry entry, User user) {
        Integer score = ratingRepository.findByUserIdAndSeriesId(user.getId(), entry.getSeries().getId())
                .map(rating -> rating.getScore())
                .orElse(null);

        return ListEntryDto.builder()
                .id(entry.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .seriesId(entry.getSeries().getId())
                .seriesTitle(entry.getSeries().getTitle())
                .seriesCoverImageUrl(entry.getSeries().getCoverImageUrl())
                .status(entry.getStatus())
                .progress(entry.getProgress())
                .favorite(entry.isFavorite())
                .userScore(score)
                .lastUpdated(entry.getLastUpdated())
                .build();
    }

    private Series findSeries(UUID seriesId) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new EntityNotFoundException("Series not found: " + seriesId));
    }
}
