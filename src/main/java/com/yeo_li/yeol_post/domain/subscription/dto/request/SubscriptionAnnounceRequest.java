package com.yeo_li.yeol_post.domain.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "공지 메일 발송 요청")
public record SubscriptionAnnounceRequest(
    @Schema(description = "공지 메일 제목", example = "서비스 점검 안내", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    String title,
    @Schema(
        description = "공지 본문 HTML(마크다운 렌더링 결과)",
        example = "<h2>점검 일정</h2><p>2026-02-20 02:00~04:00 동안 서비스 점검이 진행됩니다.</p>",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    String content
) {

}
