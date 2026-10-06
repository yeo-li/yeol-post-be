package com.yeo_li.yeol_post.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글/답글 생성 요청")
public record CommentCreateRequest(
    @Schema(description = "댓글 내용", example = "좋은 글 감사합니다!")
    @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "댓글은 1000자 이하로 입력해주세요.")
    String content,
    @Schema(description = "비로그인 댓글에 사용할 랜덤 닉네임. 닉네임 생성 API 응답값만 사용할 수 있습니다.", example = "포근한토끼")
    String anonymousNickname
) {

    public CommentCreateRequest(String content) {
        this(content, null);
    }

}
