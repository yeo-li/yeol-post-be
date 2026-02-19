package com.yeo_li.yeol_post.domain.like.controller;

import com.yeo_li.yeol_post.domain.like.dto.LikeResponse;
import com.yeo_li.yeol_post.domain.like.service.LikeService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import com.yeo_li.yeol_post.global.common.swagger.LikeResponseApiResponse;
import com.yeo_li.yeol_post.global.common.swagger.VoidApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Like", description = "좋아요 조회/등록/취소 API")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 상태 조회", description = "게시물의 좋아요 수와 현재 사용자의 좋아요 여부를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = LikeResponseApiResponse.class))
        )
    })
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> getLikes(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "조회할 게시물 ID", example = "10")
        @PathVariable("postId") Long postId
    ) {
        LikeResponse response = likeService.getLikes(principal, postId);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "좋아요 등록", description = "로그인한 사용자가 게시물에 좋아요를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "등록 성공",
            content = @Content(schema = @Schema(implementation = VoidApiResponse.class))
        )
    })
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> like(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "좋아요를 등록할 게시물 ID", example = "10")
        @PathVariable("postId") Long postId
    ) {
        likeService.like(principal, postId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    @Operation(summary = "좋아요 취소", description = "로그인한 사용자가 게시물의 좋아요를 취소합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "취소 성공",
            content = @Content(schema = @Schema(implementation = VoidApiResponse.class))
        )
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> unlike(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "좋아요를 취소할 게시물 ID", example = "10")
        @PathVariable("postId") Long postId
    ) {
        likeService.unlike(principal, postId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }
}
