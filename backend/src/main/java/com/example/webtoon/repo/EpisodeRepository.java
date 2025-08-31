package com.example.webtoon.repo;

import com.example.webtoon.domain.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EpisodeRepository extends JpaRepository<Episode, UUID> {
}
