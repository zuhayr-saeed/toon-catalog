package com.example.webtoon.repo;

import com.example.webtoon.domain.ScraperRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScraperRunRepository extends JpaRepository<ScraperRun, UUID> { }