package com.yeo_li.yeol_post.domain.subscription.domain;

import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Subscription extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String verifyToken = UUID.randomUUID().toString();

    @NotNull
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.SUBSCRIBE;

    @NotNull
    private LocalDateTime subscribedAt;

    @Nullable
    private LocalDateTime unsubscribedAt;

    protected Subscription() {

    }

    public Subscription(String email) {
        this.email = email;
        this.verifyToken = UUID.randomUUID().toString();
        this.subscribedAt = LocalDateTime.now();
        this.unsubscribedAt = null;
        this.subscriptionStatus = SubscriptionStatus.SUBSCRIBE;
    }
}

