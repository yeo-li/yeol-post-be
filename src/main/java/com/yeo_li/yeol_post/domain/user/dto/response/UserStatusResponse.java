package com.yeo_li.yeol_post.domain.user.dto.response;

import com.yeo_li.yeol_post.domain.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 로그인 사용자 상태 응답")
public record UserStatusResponse(
    @Schema(description = "로그인 여부", example = "true")
    boolean isLoggedIn,
    @Schema(description = "닉네임", example = "yeoli")
    String nickname,
    @Schema(description = "온보딩 완료 여부", example = "false")
    boolean isOnboardingComplete,
    @Schema(description = "사용자 역할", example = "USER")
    Role role
) {

}
