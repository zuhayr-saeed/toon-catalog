package com.example.webtoon.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class FollowDto {
    String username;
    Instant followedAt;
}
