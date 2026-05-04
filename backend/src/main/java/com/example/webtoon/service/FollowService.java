package com.example.webtoon.service;

import com.example.webtoon.domain.Follow;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.FollowDto;
import com.example.webtoon.repo.FollowRepository;
import com.example.webtoon.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void followUser(User follower, String targetUsername) {
        User following = findByUsername(targetUsername);
        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow follow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            followRepository.save(follow);
        }
    }

    @Transactional
    public void unfollowUser(User follower, String targetUsername) {
        User following = findByUsername(targetUsername);
        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(User follower, String targetUsername) {
        User following = findByUsername(targetUsername);
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    @Transactional(readOnly = true)
    public Page<FollowDto> getFollowers(String username, Pageable pageable) {
        User user = findByUsername(username);
        return followRepository.findByFollowing(user, pageable)
                .map(this::toFollowerDto);
    }

    @Transactional(readOnly = true)
    public Page<FollowDto> getFollowing(String username, Pageable pageable) {
        User user = findByUsername(username);
        return followRepository.findByFollower(user, pageable)
                .map(this::toFollowingDto);
    }

    @Transactional(readOnly = true)
    public long countFollowers(User user) {
        return followRepository.countByFollowing(user);
    }

    @Transactional(readOnly = true)
    public long countFollowing(User user) {
        return followRepository.countByFollower(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    private FollowDto toFollowerDto(Follow follow) {
        return FollowDto.builder()
                .username(follow.getFollower().getUsername())
                .followedAt(follow.getCreatedAt())
                .build();
    }

    private FollowDto toFollowingDto(Follow follow) {
        return FollowDto.builder()
                .username(follow.getFollowing().getUsername())
                .followedAt(follow.getCreatedAt())
                .build();
    }
}
