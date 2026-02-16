package com.yeo_li.yeol_post.domain.user.dto.request;

public record UserUpdateRequest(
    String nickname,
    String email,
    Boolean emailOptIn
) {

}
