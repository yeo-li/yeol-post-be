package com.yeo_li.yeol_post.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AdminPasswordUpdateRequest(
    @JsonProperty("current_password")
    String currentPassword,
    @JsonProperty("new_password")
    String newPassword
) {

}
