package com.example.webtoon.repo;

import com.example.webtoon.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SeriesRepository extends JpaRepository<Series, UUID> {
}
