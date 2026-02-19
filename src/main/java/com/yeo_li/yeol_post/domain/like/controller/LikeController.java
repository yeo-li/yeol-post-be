package com.yeo_li.yeol_post.domain.like.controller;

import com.yeo_li.yeol_post.domain.like.dto.LikeResponse;
import com.yeo_li.yeol_post.domain.like.service.LikeService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> getLikes(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable("postId") Long postId
    ) {
        LikeResponse response = likeService.getLikes(principal, postId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> like(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable("postId") Long postId
    ) {
        likeService.like(principal, postId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> unlike(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable("postId") Long postId
    ) {
        likeService.unlike(principal, postId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }
}
