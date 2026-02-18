package com.yeo_li.yeol_post.domain.subscription.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubscriptionAnnounceRequest(
    @NotBlank
    String title,
    @NotBlank
    String content
) {

}
