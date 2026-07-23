package com.yeo_li.yeol_post.domain.feed.dto.request;

import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "피드 생성 요청")
public record FeedCreateRequest(
    @Schema(description = "피드 내용", example = "오늘의 짧은 기록")
    @NotBlank(message = "피드 내용은 비어 있을 수 없습니다.")
    @Size(max = 2000, message = "피드는 2000자 이하로 입력해주세요.")
    String content,

    @Schema(description = "피드 접근 권한", example = "PUBLIC")
    @NotNull(message = "피드 접근 권한은 필수입니다.")
    ContentAccessLevel requiredAccessLevel
) {

}
