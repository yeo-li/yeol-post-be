package com.yeo_li.yeol_post.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 수정 요청")
public record CommentUpdateRequest(
    @Schema(description = "수정할 댓글 내용", example = "수정된 댓글입니다.")
    @NotNull String content
) {

}
