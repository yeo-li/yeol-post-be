package com.yeo_li.yeol_post.domain.subscription.repository;

import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.domain.SubscriptionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findSubscriptionByEmail(String email);

    Subscription findSubscriptionByVerifyToken(String token);

    List<Subscription> findSubscriptionsBySubscriptionStatus(SubscriptionStatus status);
}
