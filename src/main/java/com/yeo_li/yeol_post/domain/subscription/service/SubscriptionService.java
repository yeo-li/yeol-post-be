package com.yeo_li.yeol_post.domain.subscription.service;

import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.domain.SubscriptionStatus;
import com.yeo_li.yeol_post.domain.subscription.dto.SubscriptionCountResponse;
import com.yeo_li.yeol_post.domain.subscription.exception.SubscriptionType;
import com.yeo_li.yeol_post.domain.subscription.facade.SubscriptionRepositoryFacade;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepositoryFacade subscriptionRepositoryFacade;
    private final NewsLetterService newsLetterService;

    @Transactional
    public Subscription saveSubscription(String email) {
        return subscriptionRepositoryFacade.save(new Subscription(email));
    }

    @Transactional
    public void subscribe(String email) {
        validate(email);

        Subscription subscription = subscriptionRepositoryFacade.findNotificationByEmail(email);

        if (subscription == null) {
            Subscription newSubscription = subscriptionRepositoryFacade.save(
                new Subscription(email));
            newsLetterService.sendSubscribedNotification(newSubscription);
            return;
        }

        if (subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE) {
            return;
        }

        subscription.setSubscribedAt(LocalDateTime.now());
        subscription.setSubscriptionStatus(SubscriptionStatus.SUBSCRIBE);
        subscription.setUnsubscribedAt(null);
        newsLetterService.sendSubscribedNotification(subscription);
    }

    private void validate(String email) {
        if (!(email.contains("@") && email.contains("."))) {
            throw new GeneralException(SubscriptionType.EMAIL_INVALID);
        }
    }

    @Transactional
    public void unsubscribe(String token) {
        Subscription subscription = subscriptionRepositoryFacade.finaNotificationByVerifyToken(
            token);

        if (subscription == null) {
            throw new GeneralException(SubscriptionType.NOTIFICATION_NOT_FOUND);
        }

        if (subscription.getSubscriptionStatus() == SubscriptionStatus.UNSUBSCRIBE) {
            return;
        }

        subscription.setUnsubscribedAt(LocalDateTime.now());
        subscription.setSubscriptionStatus(SubscriptionStatus.UNSUBSCRIBE);
        newsLetterService.sendUnsubscribedNotification(subscription);
    }

    public List<Subscription> getSubscribedEmail() {
        return subscriptionRepositoryFacade.findNotificationsBySubscriptionStatus(
            SubscriptionStatus.SUBSCRIBE);
    }

    public SubscriptionCountResponse getSubscriptionCount() {
        int count = subscriptionRepositoryFacade.findNotificationsBySubscriptionStatus(
            SubscriptionStatus.SUBSCRIBE).size();
        return new SubscriptionCountResponse(count);
    }

    public Subscription getSubscriptionByEmail(String email) {
        return subscriptionRepositoryFacade.findSubscriptionByEmail(email);
    }
}
