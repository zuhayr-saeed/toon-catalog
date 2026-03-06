package com.example.webtoon.service;

import com.example.webtoon.domain.Series;
import com.example.webtoon.dto.SeriesDto;
import com.example.webtoon.mapper.SeriesMapper;
import com.example.webtoon.repo.SeriesRepository;
import com.google.gson.Gson;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.Searchable;
import com.meilisearch.sdk.model.Settings;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private static final String INDEX_NAME = "series";

    private final Client meilisearchClient;
    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;
    private final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        configureIndex();
        reindexAllSeries();
    }

    public void configureIndex() {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);
            Settings settings = new Settings()
                    .setSearchableAttributes(new String[]{"title", "synopsis", "genres", "tags", "authors"})
                    .setFilterableAttributes(new String[]{"type", "genres", "tags", "authors"})
                    .setSortableAttributes(new String[]{"avgRating", "ratingCount", "createdAt", "title"})
                    .setDisplayedAttributes(new String[]{
                            "id", "title", "type", "synopsis", "coverImageUrl",
                            "genres", "tags", "authors", "createdAt", "avgRating", "ratingCount"
                    });

            index.updateSettings(settings);
        } catch (Exception e) {
            log.warn("Meilisearch settings update skipped: {}", e.getMessage());
        }
    }

    public void reindexAllSeries() {
        try {
            List<Map<String, Object>> docs = seriesRepository.findAll().stream()
                    .map(this::toSearchDocument)
                    .toList();
            Index index = meilisearchClient.index(INDEX_NAME);
            index.deleteAllDocuments();
            if (!docs.isEmpty()) {
                index.addDocuments(gson.toJson(docs));
            }
            log.info("Reindexed {} series documents in Meilisearch", docs.size());
        } catch (Exception e) {
            log.warn("Meilisearch reindex skipped: {}", e.getMessage());
        }
    }

    public void indexSeries(Series series) {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);
            index.addDocuments(gson.toJson(List.of(toSearchDocument(series))));
        } catch (Exception e) {
            log.warn("Meilisearch add/update skipped for {}: {}", series.getId(), e.getMessage());
        }
    }

    public void updateSeries(Series series) {
        indexSeries(series);
    }

    public void deleteSeries(UUID seriesId) {
        try {
            meilisearchClient.index(INDEX_NAME).deleteDocument(seriesId.toString());
        } catch (Exception e) {
            log.warn("Meilisearch delete skipped for {}: {}", seriesId, e.getMessage());
        }
    }

    public List<SeriesDto> search(String query, String genre, String tag, String sortBy, int limit, int offset) {
        try {
            Index index = meilisearchClient.index(INDEX_NAME);
            SearchRequest request = new SearchRequest(query == null ? "" : query)
                    .setLimit(limit)
                    .setOffset(offset);

            String filter = buildFilter(genre, tag);
            if (!filter.isBlank()) {
                request.setFilter(new String[]{filter});
            }

            String sortExpression = mapSort(sortBy);
            if (sortExpression != null) {
                request.setSort(new String[]{sortExpression});
            }

            Searchable result = index.search(request);
            List<HashMap<String, Object>> hits = result.getHits();
            return hits.stream()
                    .map(hit -> gson.fromJson(gson.toJson(hit), SeriesDto.class))
                    .toList();
        } catch (Exception e) {
            log.warn("Meilisearch query failed, fallback to DB. query='{}' error={}", query, e.getMessage());
            return seriesRepository.findAll().stream()
                    .map(seriesMapper::toDto)
                    .filter(series -> matches(series, query, genre, tag))
                    .skip(offset)
                    .limit(limit)
                    .toList();
        }
    }

    private boolean matches(SeriesDto series, String query, String genre, String tag) {
        boolean queryMatch = query == null || query.isBlank()
                || contains(series.getTitle(), query)
                || contains(series.getSynopsis(), query);

        boolean genreMatch = genre == null || genre.isBlank()
                || series.getGenres().stream().anyMatch(g -> g.equalsIgnoreCase(genre));

        boolean tagMatch = tag == null || tag.isBlank()
                || series.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(tag));

        return queryMatch && genreMatch && tagMatch;
    }

    private boolean contains(String value, String token) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains(token.trim().toLowerCase());
    }

    private String mapSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return null;
        }
        return switch (sortBy) {
            case "top_rated" -> "avgRating:desc";
            case "popular" -> "ratingCount:desc";
            case "newest" -> "createdAt:desc";
            case "title" -> "title:asc";
            default -> null;
        };
    }

    private String buildFilter(String genre, String tag) {
        StringBuilder filter = new StringBuilder();
        if (genre != null && !genre.isBlank()) {
            filter.append("genres = \\\"" + genre.trim() + "\\\"");
        }
        if (tag != null && !tag.isBlank()) {
            if (filter.length() > 0) {
                filter.append(" AND ");
            }
            filter.append("tags = \\\"" + tag.trim() + "\\\"");
        }
        return filter.toString();
    }

    private Map<String, Object> toSearchDocument(Series series) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", series.getId().toString());
        doc.put("title", series.getTitle());
        doc.put("type", series.getType());
        doc.put("synopsis", series.getSynopsis());
        doc.put("coverImageUrl", series.getCoverImageUrl());
        doc.put("genres", series.getGenres());
        doc.put("tags", series.getTags());
        doc.put("authors", series.getAuthors());
        doc.put("createdAt", series.getCreatedAt());
        doc.put("avgRating", series.getAvgRating());
        doc.put("ratingCount", series.getRatingCount());
        return doc;
    }
}
