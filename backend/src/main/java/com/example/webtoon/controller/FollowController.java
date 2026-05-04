package com.example.webtoon.controller;

import com.example.webtoon.domain.User;
import com.example.webtoon.dto.FollowDto;
import com.example.webtoon.service.FollowService;
import com.example.webtoon.web.Pageables;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {
    private static final Set<String> FOLLOW_SORTS = Set.of("createdAt");

    private final FollowService followService;

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> followUser(@PathVariable String username,
                                           Authentication authentication) {
        User follower = (User) authentication.getPrincipal();
        followService.followUser(follower, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable String username,
                                             Authentication authentication) {
        User follower = (User) authentication.getPrincipal();
        followService.unfollowUser(follower, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/follow/status")
    public ResponseEntity<Boolean> isFollowing(@PathVariable String username,
                                               Authentication authentication) {
        User follower = (User) authentication.getPrincipal();
        return ResponseEntity.ok(followService.isFollowing(follower, username));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<FollowDto>> getFollowers(@PathVariable String username, Pageable pageable) {
        Pageable safePageable = Pageables.bounded(pageable, FOLLOW_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(followService.getFollowers(username, safePageable));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<FollowDto>> getFollowing(@PathVariable String username, Pageable pageable) {
        Pageable safePageable = Pageables.bounded(pageable, FOLLOW_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(followService.getFollowing(username, safePageable));
    }
}
