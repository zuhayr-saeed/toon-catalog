package com.example.webtoon.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Pageables {
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final int MAX_SEARCH_LIMIT = 50;
    public static final int MAX_SEARCH_OFFSET = 10_000;

    private Pageables() {
    }

    public static Pageable bounded(int page, int size, Sort sort) {
        int safePage = Math.max(page, 0);
        int safeSize = clamp(size, 1, MAX_SIZE);
        return PageRequest.of(safePage, safeSize, sort == null ? Sort.unsorted() : sort);
    }

    public static Pageable bounded(Pageable pageable, Set<String> allowedSortProperties, Sort defaultSort) {
        int page = pageable == null || pageable.isUnpaged() ? 0 : pageable.getPageNumber();
        int size = pageable == null || pageable.isUnpaged() ? DEFAULT_SIZE : pageable.getPageSize();
        Sort sort = sanitizeSort(pageable == null ? Sort.unsorted() : pageable.getSort(), allowedSortProperties, defaultSort);
        return bounded(page, size, sort);
    }

    public static Pageable fromSortParam(
            int page,
            int size,
            String sortParam,
            Set<String> allowedSortProperties,
            String defaultProperty,
            Sort.Direction defaultDirection
    ) {
        Sort sort = parseSort(sortParam, allowedSortProperties, defaultProperty, defaultDirection);
        return bounded(page, size, sort);
    }

    public static int boundedSearchLimit(int limit) {
        return clamp(limit, 1, MAX_SEARCH_LIMIT);
    }

    public static int boundedSearchOffset(int offset) {
        return clamp(offset, 0, MAX_SEARCH_OFFSET);
    }

    private static Sort parseSort(
            String sortParam,
            Set<String> allowedSortProperties,
            String defaultProperty,
            Sort.Direction defaultDirection
    ) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(defaultDirection, defaultProperty);
        }

        String[] tokens = sortParam.split(",");
        String property = tokens.length > 0 ? tokens[0].trim() : defaultProperty;
        Sort.Direction direction = tokens.length > 1
                ? Sort.Direction.fromOptionalString(tokens[1].trim())
                        .orElseThrow(() -> new IllegalArgumentException("Unsupported sort direction: " + tokens[1].trim()))
                : defaultDirection;

        validateSortProperty(property, allowedSortProperties);
        return Sort.by(direction, property);
    }

    private static Sort sanitizeSort(Sort sort, Set<String> allowedSortProperties, Sort defaultSort) {
        if (sort == null || sort.isUnsorted()) {
            return defaultSort == null ? Sort.unsorted() : defaultSort;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            validateSortProperty(order.getProperty(), allowedSortProperties);
            orders.add(order);
        }
        return Sort.by(orders);
    }

    private static void validateSortProperty(String property, Set<String> allowedSortProperties) {
        if (property == null || property.isBlank() || !allowedSortProperties.contains(property)) {
            throw new IllegalArgumentException("Unsupported sort property: " + property);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
