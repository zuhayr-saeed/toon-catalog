package com.example.webtoon.controller;

import com.example.webtoon.domain.ReadingStatus;
import com.example.webtoon.domain.User;
import com.example.webtoon.dto.ListEntryDto;
import com.example.webtoon.dto.ListEntryUpsertRequest;
import com.example.webtoon.service.ListEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/list")
@RequiredArgsConstructor
public class ListEntryController {

    private final ListEntryService listEntryService;

    @GetMapping
    public ResponseEntity<Page<ListEntryDto>> getMyList(
            @RequestParam(required = false) ReadingStatus status,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastUpdated,desc") String sort,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Pageable pageable = pageRequest(page, size, sort);
        return ResponseEntity.ok(listEntryService.getMyList(user, status, favorite, pageable));
    }

    @GetMapping("/{seriesId}")
    public ResponseEntity<ListEntryDto> getMyListEntry(@PathVariable UUID seriesId,
                                                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return listEntryService.getMyListEntry(user, seriesId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{seriesId}")
    public ResponseEntity<ListEntryDto> upsert(@PathVariable UUID seriesId,
                                               @Valid @RequestBody ListEntryUpsertRequest request,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(listEntryService.upsert(user, seriesId, request));
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> delete(@PathVariable UUID seriesId,
                                       Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        listEntryService.delete(user, seriesId);
        return ResponseEntity.noContent().build();
    }

    private Pageable pageRequest(int page, int size, String sort) {
        String[] sortTokens = sort.split(",");
        String property = sortTokens.length > 0 ? sortTokens[0] : "lastUpdated";
        Sort.Direction direction = sortTokens.length > 1
                ? Sort.Direction.fromOptionalString(sortTokens[1]).orElse(Sort.Direction.DESC)
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
