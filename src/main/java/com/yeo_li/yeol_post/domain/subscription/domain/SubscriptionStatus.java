package com.yeo_li.yeol_post.domain.subscription.domain;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    SUBSCRIBE("SUBSCRIBE"),
    UNSUBSCRIBE("UNSUBSCRIBE");

    private final String status;


    SubscriptionStatus(String status) {
        this.status = status;
    }
}
