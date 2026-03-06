package com.example.webtoon.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingRequest {
    @NotNull
    @Min(1)
    @Max(10)
    private Integer score;

    private String review;
}
