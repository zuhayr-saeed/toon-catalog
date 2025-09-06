package com.example.webtoon.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingSummary {
    private UUID seriesId;
    private double avgRating;
    private int ratingCount;
}