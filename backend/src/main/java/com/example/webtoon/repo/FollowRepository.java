package com.example.webtoon.repo;

import com.example.webtoon.domain.Follow;
import com.example.webtoon.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    @EntityGraph(attributePaths = "following")
    Page<Follow> findByFollower(User follower, Pageable pageable);

    @EntityGraph(attributePaths = "follower")
    Page<Follow> findByFollowing(User following, Pageable pageable);

    boolean existsByFollowerAndFollowing(User follower, User following);
    long countByFollower(User follower);
    long countByFollowing(User following);
}
