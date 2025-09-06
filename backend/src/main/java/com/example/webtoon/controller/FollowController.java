package com.example.webtoon.controller;

import com.example.webtoon.domain.Follow;
import com.example.webtoon.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable UUID id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        UUID followerId = UUID.fromString(userDetails.getUsername());
        followService.followUser(followerId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable UUID id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        UUID followerId = UUID.fromString(userDetails.getUsername());
        followService.unfollowUser(followerId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<Follow>> getFollowers(@PathVariable UUID id) {
        return ResponseEntity.ok(followService.getFollowers(id));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<Follow>> getFollowing(@PathVariable UUID id) {
        return ResponseEntity.ok(followService.getFollowing(id));
    }
}