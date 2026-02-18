package com.yeo_li.yeol_post.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 프로필 응답")
public record UserProfileResponse(
    @Schema(description = "이름", example = "홍길동")
    String name,
    @Schema(description = "닉네임", example = "yeoli")
    String nickname,
    @Schema(description = "이메일", example = "yeoli@example.com")
    String email,
    @Schema(description = "이메일 구독 여부", example = "true")
    boolean isSubscribed
) {

}
