package com.example.webtoon.repo;

import com.example.webtoon.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SeriesRepository extends JpaRepository<Series, UUID> {
    // <-- Add this Finder  
    Optional<Series> findByExternalId(String externalId);  
  
    // Later, for recommendations (top-n in genre)  
    // List<Series> findTop10ByGenreOrderByAvgRatingDesc(String genre);
}
