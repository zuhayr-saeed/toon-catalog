package com.example.webtoon.dto;

import com.example.webtoon.domain.ReadingStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ListEntryUpsertRequest {
    private ReadingStatus status;

    @Min(0)
    private Integer progress;

    private Boolean favorite;
}
