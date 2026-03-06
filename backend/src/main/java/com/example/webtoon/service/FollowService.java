package com.example.webtoon.service;

import com.example.webtoon.domain.Follow;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.FollowDto;
import com.example.webtoon.repo.FollowRepository;
import com.example.webtoon.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<FollowDto> getFollowers(String username) {
        User user = findByUsername(username);
        return followRepository.findByFollowing(user).stream()
                .map(follow -> FollowDto.builder()
                        .username(follow.getFollower().getUsername())
                        .followedAt(follow.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FollowDto> getFollowing(String username) {
        User user = findByUsername(username);
        return followRepository.findByFollower(user).stream()
                .map(follow -> FollowDto.builder()
                        .username(follow.getFollowing().getUsername())
                        .followedAt(follow.getCreatedAt())
                        .build())
                .toList();
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
}
