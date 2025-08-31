package com.example.webtoon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingSummary {
    private double average;
    private long count;
}
