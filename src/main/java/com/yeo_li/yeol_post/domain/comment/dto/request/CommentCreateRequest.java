package com.yeo_li.yeol_post.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글/답글 생성 요청")
public record CommentCreateRequest(
    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    @NotNull String content
) {

}
