package com.example.webtoon.dto;

import com.example.webtoon.domain.ReadingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ListEntryUpsertRequest {
    private ReadingStatus status;

    @Min(0)
    @Max(10000)
    private Integer progress;

    private Boolean favorite;
}
