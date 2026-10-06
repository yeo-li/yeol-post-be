package com.yeo_li.yeol_post.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비로그인 댓글용 랜덤 닉네임 응답")
public record AnonymousNicknameResponse(
    @Schema(description = "생성된 랜덤 닉네임", example = "포근한토끼")
    String nickname
) {

}
