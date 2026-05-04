package com.example.webtoon.bootstrap;

import com.example.webtoon.domain.Episode;
import com.example.webtoon.domain.Series;
import com.example.webtoon.repo.EpisodeRepository;
import com.example.webtoon.repo.SeriesRepository;
import com.example.webtoon.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Optional seed data so a fresh deployment is browsable immediately.
 *
 * Activation: {@code app.bootstrap.seed-demo=true}.
 *
 * The runner is idempotent: it only inserts demo series whose external_id starts with
 * {@code demo:} that aren't already present, and gives each series a small set of episodes.
 * Real data is never touched.
 */
@Component
@Order(10)
@Slf4j
public class DemoSeedRunner implements ApplicationRunner {

    private final SeriesRepository seriesRepository;
    private final EpisodeRepository episodeRepository;
    private final SearchService searchService;

    @Value("${app.bootstrap.seed-demo:false}")
    private boolean enabled;

    public DemoSeedRunner(SeriesRepository seriesRepository,
                          EpisodeRepository episodeRepository,
                          SearchService searchService) {
        this.seriesRepository = seriesRepository;
        this.episodeRepository = episodeRepository;
        this.searchService = searchService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        int created = 0;
        for (DemoSeries demo : DEMO_SERIES) {
            if (seriesRepository.findByExternalId(demo.externalId).isPresent()) {
                continue;
            }

            Series series = Series.builder()
                    .title(demo.title)
                    .type(demo.type)
                    .synopsis(demo.synopsis)
                    .coverImageUrl(demo.coverImageUrl)
                    .genres(new LinkedHashSet<>(demo.genres))
                    .tags(new LinkedHashSet<>(demo.tags))
                    .authors(new LinkedHashSet<>(demo.authors))
                    .externalId(demo.externalId)
                    .build();
            Series saved = seriesRepository.save(series);

            for (int i = 1; i <= demo.episodes; i++) {
                episodeRepository.save(Episode.builder()
                        .series(saved)
                        .number(i)
                        .title("Episode " + i)
                        .releaseDate(LocalDate.now().minusDays((long) (demo.episodes - i) * 7))
                        .build());
            }

            try {
                searchService.indexSeries(saved);
            } catch (Exception ex) {
                log.warn("Demo seed: failed to index '{}' in search: {}", saved.getTitle(), ex.getMessage());
            }
            created++;
        }

        if (created > 0) {
            log.info("Demo seed: inserted {} demo series", created);
        } else {
            log.info("Demo seed: all demo series already present");
        }
    }

    private record DemoSeries(
            String externalId,
            String title,
            String type,
            String synopsis,
            String coverImageUrl,
            Set<String> genres,
            Set<String> tags,
            Set<String> authors,
            int episodes
    ) {
    }

    private static final List<DemoSeries> DEMO_SERIES = List.of(
            new DemoSeries(
                    "demo:tower-of-glass",
                    "Tower of Glass",
                    "WEBTOON",
                    "A young climber ascends an impossible tower in pursuit of a friend who vanished above the clouds.",
                    "https://picsum.photos/seed/tower-of-glass/400/600",
                    Set.of("Action", "Fantasy"),
                    Set.of("Adventure", "Magic"),
                    Set.of("A. Park"),
                    18
            ),
            new DemoSeries(
                    "demo:midnight-cafe",
                    "Midnight Cafe",
                    "WEBTOON",
                    "A late-night cafe in Seoul serves drinks that surface the customer's most honest memory.",
                    "https://picsum.photos/seed/midnight-cafe/400/600",
                    Set.of("Slice of Life", "Romance"),
                    Set.of("Cozy", "Episodic"),
                    Set.of("Y. Han"),
                    24
            ),
            new DemoSeries(
                    "demo:steel-saint",
                    "Steel Saint",
                    "WEBTOON",
                    "A retired knight returns from exile to find her order replaced by an empire of polished steel.",
                    "https://picsum.photos/seed/steel-saint/400/600",
                    Set.of("Action", "Drama"),
                    Set.of("Knights", "Politics"),
                    Set.of("J. Choi"),
                    12
            ),
            new DemoSeries(
                    "demo:noodle-shop-detective",
                    "Noodle Shop Detective",
                    "WEBTOON",
                    "A grumpy chef solves neighborhood mysteries one bowl at a time.",
                    "https://picsum.photos/seed/noodle-shop/400/600",
                    Set.of("Mystery", "Comedy"),
                    Set.of("Episodic", "Food"),
                    Set.of("M. Lee"),
                    20
            ),
            new DemoSeries(
                    "demo:zero-gravity-class",
                    "Zero Gravity Class",
                    "WEBNOVEL",
                    "Six exchange students at humanity's first orbital high school discover the station is hiding a passenger.",
                    "https://picsum.photos/seed/zero-gravity/400/600",
                    Set.of("Sci-Fi", "Mystery"),
                    Set.of("School", "Space"),
                    Set.of("R. Tanaka"),
                    9
            ),
            new DemoSeries(
                    "demo:ink-and-thunder",
                    "Ink and Thunder",
                    "WEBTOON",
                    "A calligrapher who hears storms before they arrive is conscripted into a court of weather diviners.",
                    "https://picsum.photos/seed/ink-thunder/400/600",
                    Set.of("Fantasy", "Drama"),
                    Set.of("Court Intrigue", "Magic"),
                    Set.of("S. Kim"),
                    16
            )
    );
}
