package com.example.webtoon.repo;

import com.example.webtoon.domain.ListEntry;
import com.example.webtoon.domain.ReadingStatus;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ListEntryRepository extends JpaRepository<ListEntry, UUID> {

    @EntityGraph(attributePaths = "series")
    Page<ListEntry> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "series")
    Page<ListEntry> findByUserAndStatus(User user, ReadingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "series")
    Page<ListEntry> findByUserAndFavorite(User user, Boolean favorite, Pageable pageable);

    @EntityGraph(attributePaths = "series")
    Page<ListEntry> findByUserAndStatusAndFavorite(User user, ReadingStatus status, Boolean favorite, Pageable pageable);

    @EntityGraph(attributePaths = "series")
    Page<ListEntry> findByUserAndFavoriteTrue(User user, Pageable pageable);

    @EntityGraph(attributePaths = "series")
    Optional<ListEntry> findByUserAndSeries(User user, Series series);

    boolean existsByUserAndSeries(User user, Series series);
    void deleteByUserAndSeries(User user, Series series);
    long countByUserAndStatus(User user, ReadingStatus status);
    long countByUserAndFavoriteTrue(User user);
}
