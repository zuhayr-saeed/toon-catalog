package com.example.webtoon.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummary {
    private UUID seriesId;
    private double avg;
    private int count;
}
