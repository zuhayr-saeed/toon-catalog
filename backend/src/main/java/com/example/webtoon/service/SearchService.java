package com.example.webtoon.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesDto;
import com.google.gson.Gson;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.Searchable;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TaskInfo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final Client meilisearchClient;
    private static final String INDEX_NAME = "series";
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);

            Settings settings = new Settings()
                    .setSearchableAttributes(new String[]{"title", "author", "description", "genre"})
                    .setFilterableAttributes(new String[]{"genre", "author"})
                    .setDisplayedAttributes(new String[]{
                            "id", "title", "author", "description",
                            "genre", "coverImage", "avgRating", "ratingCount"
                    });

            TaskInfo task = index.updateSettings(settings);
            log.info("Meilisearch index settings update task enqueued: {}", task.getTaskUid());

        } catch (Exception e) {
            log.error("Failed to initialize Meilisearch index", e);
        }
    }

    public void indexSeries(Series series) {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);
            TaskInfo task = index.addDocuments(gson.toJson(series));
            log.debug("Series indexed: {} (task: {})", series.getTitle(), task.getTaskUid());
        } catch (Exception e) {
            log.error("Error indexing series {}: {}", series.getTitle(), e.getMessage());
        }
    }

    public void updateSeries(Series series) {
        indexSeries(series); // addDocuments does upsert
    }

    public void deleteSeries(String seriesId) {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);
            TaskInfo task = index.deleteDocument(seriesId);
            log.debug("Deleted series {} (task: {})", seriesId, task.getTaskUid());
        } catch (Exception e) {
            log.error("Error deleting series {}: {}", seriesId, e.getMessage());
        }
    }

    public List<SeriesDto> search(String query, int limit, int offset) {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);

            SearchRequest request = new SearchRequest(query)
                    .setLimit(limit)
                    .setOffset(offset);

            Searchable result = index.search(request);
            List<HashMap<String,Object>> hits = result.getHits();

            return hits.stream()
                    .map(hit -> gson.fromJson(gson.toJson(hit), SeriesDto.class))
                    .toList();

        } catch (Exception e) {
            log.error("Search failed for '{}'", query, e);
            return List.of();
        }
    }

    public List<SeriesDto> searchByGenre(String genre, int limit, int offset) {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);

            SearchRequest request = new SearchRequest("")
                    .setLimit(limit)
                    .setOffset(offset)
                    .setFilter(new String[]{String.format("genre = \"%s\"", genre)});

            Searchable result = index.search(request);
            List<HashMap<String,Object>> hits = result.getHits();

            return hits.stream()
                    .map(hit -> gson.fromJson(gson.toJson(hit), SeriesDto.class))
                    .toList();

        } catch (Exception e) {
            log.error("Search by genre failed for '{}'", genre, e);
            return List.of();
        }
    }
}