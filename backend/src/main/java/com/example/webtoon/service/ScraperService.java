package com.example.webtoon.service;

import com.example.webtoon.domain.ScraperRun;
import com.example.webtoon.domain.Series;
import com.example.webtoon.repo.ScraperRunRepository;
import com.example.webtoon.repo.SeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperService {

    private final SeriesRepository seriesRepository;
    private final ScraperRunRepository scraperRunRepository;

    /**
     * Runs nightly at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void runScraper() {
        ScraperRun run = ScraperRun.builder()
                .startedAt(Instant.now())
                .build();

        int added = 0, updated = 0, failed = 0;

        try {
            // TODO: Replace with jsoup/Playwright logic
            log.info("Scraper job started at {}", run.getStartedAt());

            // Example stub data:
            String externalId = "site12345";
            Series existing = seriesRepository.findByExternalId(externalId).orElse(null);

            if (existing == null) {
                // Insert new
                Series newSeries = Series.builder()
                        .title("Sample Webtoon")
                        .author("Jane Doe")
                        .genre("Romance")
                        .description("Scraped test description")
                        .coverImage("https://cdn.example.com/sample.jpg")
                        .externalId(externalId)
                        .build();
                seriesRepository.save(newSeries);
                added++;
            } else {
                // Update existing
                existing.setDescription("Updated description from scraper");
                seriesRepository.save(existing);
                updated++;
            }

        } catch (Exception e) {
            log.error("Scraper run failed: {}", e.getMessage());
            failed++;
        } finally {
            run.setSeriesAdded(added);
            run.setSeriesUpdated(updated);
            run.setSeriesFailed(failed);
            run.setFinishedAt(Instant.now());
            scraperRunRepository.save(run);

            log.info("Scraper job finished: added={}, updated={}, failed={}", added, updated, failed);
        }
    }
}