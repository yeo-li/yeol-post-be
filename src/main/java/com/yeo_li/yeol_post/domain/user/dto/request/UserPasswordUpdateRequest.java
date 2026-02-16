package com.yeo_li.yeol_post.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPasswordUpdateRequest(
    @JsonProperty("current_password")
    String currentPassword,
    @JsonProperty("new_password")
    String newPassword
) {

}
