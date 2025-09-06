package com.example.webtoon.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RatingRequest {
    private UUID seriesId;
    private int score; // 1–5
    private String review; // optional
}