package com.yeo_li.yeol_post.domain.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 구독 요청")
public record SubscriptionCreateRequest(
    @Schema(description = "구독할 이메일 주소", example = "yeoli@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Email
    String email
) {

}
