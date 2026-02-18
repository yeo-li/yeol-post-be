package com.yeo_li.yeol_post.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 수정 요청")
public record UserUpdateRequest(
    @Schema(description = "닉네임", example = "yeoli")
    String nickname,
    @Schema(description = "이메일", example = "yeoli@example.com")
    String email,
    @Schema(description = "이메일 수신 동의 여부", example = "true")
    Boolean emailOptIn
) {

}
