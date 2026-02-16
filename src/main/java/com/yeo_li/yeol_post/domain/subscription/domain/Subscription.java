package com.yeo_li.yeol_post.domain.subscription.domain;

import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false, length = 20)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.SUBSCRIBE;

    @NotNull
    private LocalDateTime subscribedAt;

    @Nullable
    private LocalDateTime unsubscribedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

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
