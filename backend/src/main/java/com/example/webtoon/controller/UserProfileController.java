package com.example.webtoon.controller;

import com.example.webtoon.domain.ReadingStatus;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.ListEntryDto;
import com.example.webtoon.dto.UserProfileDto;
import com.example.webtoon.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String username,
                                                     Authentication authentication) {
        User viewer = (authentication != null && authentication.getPrincipal() instanceof User)
                ? (User) authentication.getPrincipal()
                : null;
        return ResponseEntity.ok(userProfileService.getPublicProfile(username, viewer));
    }

    @GetMapping("/{username}/list")
    public ResponseEntity<Page<ListEntryDto>> getPublicList(
            @PathVariable String username,
            @RequestParam(required = false) ReadingStatus status,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastUpdated,desc") String sort) {

        String[] sortTokens = sort.split(",");
        String property = sortTokens.length > 0 ? sortTokens[0] : "lastUpdated";
        Sort.Direction direction = sortTokens.length > 1
                ? Sort.Direction.fromOptionalString(sortTokens[1]).orElse(Sort.Direction.DESC)
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        return ResponseEntity.ok(userProfileService.getPublicList(username, status, favorite, pageable));
    }
}
