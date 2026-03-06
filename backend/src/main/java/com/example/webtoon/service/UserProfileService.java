package com.example.webtoon.service;

import com.example.webtoon.domain.ListEntry;
import com.example.webtoon.domain.ReadingStatus;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.ListEntryDto;
import com.example.webtoon.dto.UserProfileDto;
import com.example.webtoon.repo.ListEntryRepository;
import com.example.webtoon.repo.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final FollowService followService;
    private final ListEntryRepository listEntryRepository;
    private final RatingRepository ratingRepository;

    @Transactional(readOnly = true)
    public UserProfileDto getPublicProfile(String username, User viewer) {
        User profileUser = followService.findByUsername(username);

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (ReadingStatus status : ReadingStatus.values()) {
            statusCounts.put(status.name(), listEntryRepository.countByUserAndStatus(profileUser, status));
        }

        boolean followedByMe = viewer != null && followService.isFollowing(viewer, username);

        return UserProfileDto.builder()
                .username(profileUser.getUsername())
                .joinedAt(profileUser.getCreatedAt())
                .followersCount(Math.toIntExact(followService.countFollowers(profileUser)))
                .followingCount(Math.toIntExact(followService.countFollowing(profileUser)))
                .favoritesCount(Math.toIntExact(listEntryRepository.countByUserAndFavoriteTrue(profileUser)))
                .statusCounts(statusCounts)
                .followedByMe(followedByMe)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ListEntryDto> getPublicList(String username, ReadingStatus status, Boolean favorite, Pageable pageable) {
        User profileUser = followService.findByUsername(username);
        return filterList(profileUser, status, favorite, pageable)
                .map(entry -> toListEntryDto(entry, profileUser));
    }

    private Page<ListEntry> filterList(User user, ReadingStatus status, Boolean favorite, Pageable pageable) {
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

    private ListEntryDto toListEntryDto(ListEntry entry, User owner) {
        Integer score = ratingRepository.findByUserIdAndSeriesId(owner.getId(), entry.getSeries().getId())
                .map(rating -> rating.getScore())
                .orElse(null);

        return ListEntryDto.builder()
                .id(entry.getId())
                .userId(owner.getId())
                .username(owner.getUsername())
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
}
