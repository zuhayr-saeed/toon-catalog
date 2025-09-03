package com.example.webtoon.repo;

import com.example.webtoon.domain.ListEntry;
import com.example.webtoon.domain.Series;
import com.example.webtoon.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListEntryRepository extends JpaRepository<ListEntry, UUID> {

    Page<ListEntry> findByUser(User user, Pageable pageable);
    boolean existsByUserAndSeries(User user, Series series);
    void deleteByUserAndSeries(User user, Series series);
}
