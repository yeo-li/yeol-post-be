package com.yeo_li.yeol_post.domain.feed.dto.request;

import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "피드 수정 요청")
public record FeedUpdateRequest(
    @Schema(description = "수정할 피드 내용", example = "수정된 짧은 기록")
    @Size(max = 500, message = "피드는 500자 이하로 입력해주세요.")
    String content,

    @Schema(description = "수정할 피드 접근 권한", example = "LIMITED")
    ContentAccessLevel requiredAccessLevel
) {

}
