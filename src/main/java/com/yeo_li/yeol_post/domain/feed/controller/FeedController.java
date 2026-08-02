package com.yeo_li.yeol_post.domain.feed.controller;

import com.yeo_li.yeol_post.domain.feed.dto.request.FeedCreateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.request.FeedUpdateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.response.FeedResponse;
import com.yeo_li.yeol_post.domain.feed.service.FeedService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedResponse>>> getFeeds(
        @AuthenticationPrincipal OAuth2User principal
    ) {
        List<FeedResponse> responses = feedService.getFeeds(principal);
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedResponse>> saveFeed(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody @Valid FeedCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(feedService.saveFeed(principal, request)));
    }

    @PatchMapping("/{feedId}")
    public ResponseEntity<ApiResponse<FeedResponse>> updateFeed(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long feedId,
        @RequestBody @Valid FeedUpdateRequest request
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(feedService.updateFeed(principal, feedId, request))
        );
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeed(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long feedId
    ) {
        feedService.deleteFeed(principal, feedId);
        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    @PostMapping("/{feedId}/likes")
    public ResponseEntity<ApiResponse<Void>> likeFeed(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long feedId
    ) {
        feedService.likeFeed(principal, feedId);
        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    @DeleteMapping("/{feedId}/likes")
    public ResponseEntity<ApiResponse<Void>> unlikeFeed(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long feedId
    ) {
        feedService.unlikeFeed(principal, feedId);
        return ResponseEntity.ok(ApiResponse.onSuccess());
    }
}
