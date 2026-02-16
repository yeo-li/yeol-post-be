package com.yeo_li.yeol_post.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비밀번호 수정 요청")
public record UserPasswordUpdateRequest(
    @Schema(description = "현재 비밀번호", example = "oldPassword123!")
    @JsonProperty("current_password")
    String currentPassword,
    @Schema(description = "새 비밀번호", example = "newPassword123!")
    @JsonProperty("new_password")
    String newPassword
) {

}
