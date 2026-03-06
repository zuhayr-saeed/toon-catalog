package com.example.webtoon.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class UserProfileDto {
    String username;
    Instant joinedAt;
    Integer followersCount;
    Integer followingCount;
    Integer favoritesCount;
    Map<String, Long> statusCounts;
    Boolean followedByMe;
}
