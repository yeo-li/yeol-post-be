package com.yeo_li.yeol_post.domain.admin.dto;

public record AdminUpdateRequest(
    String name,
    String nickname,
    String username,
    String email
) {

}
