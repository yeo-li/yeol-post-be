package com.yeo_li.yeol_post.domain.comment.controller;

import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentUpdateRequest;
import com.yeo_li.yeol_post.domain.comment.service.CommentService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 게시물의 댓글을 삭제할 때
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long commentId
    ) {
        commentService.deleteComment(principal, commentId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글을 수정할 때
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long commentId,
        @RequestBody @Valid CommentUpdateRequest request
    ) {
        commentService.updateComment(principal, commentId, request);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글에 답글을 달 때
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<ApiResponse<?>> saveReply(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long commentId,
        @RequestBody @Valid CommentCreateRequest request
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글에 좋아요를 누를 때
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<ApiResponse<?>> likeComment(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long commentId
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글에 좋아요를 취소할 때
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<ApiResponse<?>> unlikeComment(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long commentId
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

}
