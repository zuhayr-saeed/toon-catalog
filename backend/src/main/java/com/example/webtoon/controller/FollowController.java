package com.example.webtoon.controller;

import com.example.webtoon.domain.User;
import com.example.webtoon.dto.FollowDto;
import com.example.webtoon.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

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
    public ResponseEntity<List<FollowDto>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowers(username));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<FollowDto>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowing(username));
    }
}
