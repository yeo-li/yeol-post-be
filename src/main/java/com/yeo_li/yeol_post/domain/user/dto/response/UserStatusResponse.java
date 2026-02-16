package com.yeo_li.yeol_post.domain.user.dto.response;

public record UserStatusResponse(
    boolean isLoggedIn,
    String nickname,
    boolean isOnboardingComplete
) {

}
