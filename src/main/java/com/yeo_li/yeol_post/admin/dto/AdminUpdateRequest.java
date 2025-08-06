package com.yeo_li.yeol_post.admin.dto;

public record AdminUpdateRequest(
    String name,
    String nickname,
    String username,
    String email
) {

}
