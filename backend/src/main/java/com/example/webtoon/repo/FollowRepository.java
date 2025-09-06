package com.example.webtoon.repo;

import com.example.webtoon.domain.Follow;
import com.example.webtoon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
}