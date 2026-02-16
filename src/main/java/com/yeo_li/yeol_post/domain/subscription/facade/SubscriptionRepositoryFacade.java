package com.yeo_li.yeol_post.domain.subscription.facade;

import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.domain.SubscriptionStatus;
import com.yeo_li.yeol_post.domain.subscription.repository.SubscriptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SubscriptionRepositoryFacade {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription findNotificationByEmail(String email) {
        return subscriptionRepository.findSubscriptionByEmail(email);
    }

    public Subscription finaNotificationByVerifyToken(String token) {
        return subscriptionRepository.findSubscriptionByVerifyToken(token);
    }

    public List<Subscription> findNotificationsBySubscriptionStatus(SubscriptionStatus status) {
        return subscriptionRepository.findSubscriptionsBySubscriptionStatus(status);
    }

    public Subscription findSubscriptionByEmail(String email) {
        return subscriptionRepository.findSubscriptionByEmail(email);
    }

    @Transactional
    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}
