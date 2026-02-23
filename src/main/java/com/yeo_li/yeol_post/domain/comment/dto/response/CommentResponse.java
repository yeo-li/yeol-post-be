package com.yeo_li.yeol_post.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "댓글 응답")
public record CommentResponse(
    @Schema(description = "댓글 ID", example = "200")
    Long commentId,
    @Schema(description = "댓글 작성자 닉네임", example = "yeoli")
    String userNickname,
    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    String content,
    @Schema(description = "댓글 작성 시각", example = "2026-02-23T10:30:00")
    LocalDateTime createdAt,
    @Schema(description = "댓글 좋아요 수", example = "3")
    int likeCount,
    @Schema(description = "현재 사용자 좋아요 여부", example = "true")
    boolean isLiked,
    @Schema(description = "현재 사용자가 작성자인지 여부", example = "false")
    boolean isOwner,
    @Schema(description = "해당 댓글의 답글 목록")
    List<CommentReplyResponse> replies
) {

}
