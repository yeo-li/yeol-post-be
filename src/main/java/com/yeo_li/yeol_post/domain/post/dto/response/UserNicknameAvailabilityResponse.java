package com.yeo_li.yeol_post.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 사용 가능 여부 응답")
public record UserNicknameAvailabilityResponse(
    @Schema(description = "닉네임 사용 가능 여부 (true: 사용 가능)", example = "true")
    boolean duplicated
) {

}
