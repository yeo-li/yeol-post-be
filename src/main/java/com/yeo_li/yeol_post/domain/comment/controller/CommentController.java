package com.yeo_li.yeol_post.domain.comment.controller;

import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentUpdateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentReplyResponse;
import com.yeo_li.yeol_post.domain.comment.service.CommentService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import com.yeo_li.yeol_post.global.common.swagger.CommentReplyResponseApiResponse;
import com.yeo_li.yeol_post.global.common.swagger.VoidApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Comment", description = "댓글/답글/댓글 좋아요 API")
public class CommentController {

    private final CommentService commentService;

    // 게시물의 댓글을 삭제할 때
    @Operation(summary = "댓글 삭제", description = "댓글 ID로 댓글을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "삭제 성공",
            content = @Content(schema = @Schema(implementation = VoidApiResponse.class))
        )
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "삭제할 댓글 ID", example = "200")
        @PathVariable Long commentId
    ) {
        commentService.deleteComment(principal, commentId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글을 수정할 때
    @Operation(summary = "댓글 수정", description = "댓글 ID로 댓글 내용을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = VoidApiResponse.class))
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "댓글 수정 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "content": "수정된 댓글입니다."
                }
                """)
        )
    )
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "수정할 댓글 ID", example = "200")
        @PathVariable Long commentId,
        @RequestBody @Valid CommentUpdateRequest request
    ) {
        commentService.updateComment(principal, commentId, request);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글에 답글을 달 때
    @Operation(summary = "답글 작성", description = "댓글 ID를 부모로 답글을 작성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "작성 성공",
            content = @Content(schema = @Schema(implementation = CommentReplyResponseApiResponse.class))
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "답글 작성 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "content": "좋은 포인트 감사합니다!"
                }
                """)
        )
    )
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<ApiResponse<CommentReplyResponse>> saveReply(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "부모 댓글 ID", example = "200")
        @PathVariable Long commentId,
        @RequestBody @Valid CommentCreateRequest request
    ) {
        CommentReplyResponse response = commentService.saveReply(principal, commentId, request);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 게시물의 댓글에 좋아요를 누를 때
    @Operation(summary = "댓글 좋아요 등록", description = "댓글에 좋아요를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "등록 성공",
            content = @Content(schema = @Schema(implementation = VoidApiResponse.class))
        )
    })
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<ApiResponse<Void>> likeComment(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "좋아요를 등록할 댓글 ID", example = "200")
        @PathVariable Long commentId
    ) {
        commentService.likeComment(principal, commentId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

    // 게시물의 댓글에 좋아요를 취소할 때
    @Operation(summary = "댓글 좋아요 취소", description = "댓글의 좋아요를 취소합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "취소 성공",
            content = @Content(schema = @Schema(implementation = VoidApiResponse.class))
        )
    })
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<ApiResponse<Void>> unlikeComment(
        @AuthenticationPrincipal OAuth2User principal,
        @Parameter(description = "좋아요를 취소할 댓글 ID", example = "200")
        @PathVariable Long commentId
    ) {
        commentService.unlikeComment(principal, commentId);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

}
