package com.yeo_li.yeol_post.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "답글 응답")
public record CommentReplyResponse(
    @Schema(description = "답글 ID", example = "201")
    Long commentId,
    @Schema(description = "답글 작성자 닉네임", example = "guest")
    String userNickname,
    @Schema(description = "답글 내용", example = "저도 같은 생각입니다.")
    String content,
    @Schema(description = "답글 작성 시각", example = "2026-02-23T10:40:00")
    LocalDateTime createdAt,
    @Schema(description = "답글 좋아요 수", example = "1")
    int likeCount,
    @Schema(description = "현재 사용자 좋아요 여부", example = "false")
    boolean isLiked,
    @Schema(description = "현재 사용자가 작성자인지 여부", example = "false")
    boolean isOwner
) {

}
