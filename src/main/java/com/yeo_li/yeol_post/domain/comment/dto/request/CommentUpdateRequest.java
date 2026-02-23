package com.yeo_li.yeol_post.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글 수정 요청")
public record CommentUpdateRequest(
    @Schema(description = "수정할 댓글 내용", example = "수정된 댓글입니다.")
    @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "댓글은 1000자 이하로 입력해주세요.")
    String content
) {

}
