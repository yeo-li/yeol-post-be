package com.yeo_li.yeol_post.domain.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "구독자 수 응답")
public record SubscriptionCountResponse(
    @Schema(description = "현재 구독 중인 이메일 수", example = "128")
    int count
) {

}
