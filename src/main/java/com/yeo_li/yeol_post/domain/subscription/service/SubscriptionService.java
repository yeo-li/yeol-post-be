package com.yeo_li.yeol_post.domain.subscription.service;

import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.domain.SubscriptionStatus;
import com.yeo_li.yeol_post.domain.subscription.dto.request.SubscriptionAnnounceRequest;
import com.yeo_li.yeol_post.domain.subscription.dto.response.SubscriptionCountResponse;
import com.yeo_li.yeol_post.domain.subscription.event.AnnouncementRequestedEvent;
import com.yeo_li.yeol_post.domain.subscription.exception.SubscriptionType;
import com.yeo_li.yeol_post.domain.subscription.facade.SubscriptionRepositoryFacade;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepositoryFacade subscriptionRepositoryFacade;
    private final NewsLetterService newsLetterService;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public Subscription saveSubscription(String email) {
        Subscription subscription = subscriptionRepositoryFacade.save(new Subscription(email));

        log.info(StructuredLog.event(
                "SUBSCRIPTION_CREATED",
                "구독 정보가 생성되었습니다.",
                "CREATED"
            )
            .field("subscriptionId", subscription.getId())
            .field("status", subscription.getSubscriptionStatus())
            .build());

        return subscription;
    }

    @Transactional
    public void subscribe(String email) {
        validate(email);

        Subscription subscription = subscriptionRepositoryFacade.findNotificationByEmail(email);

        if (subscription == null) {
            Subscription newSubscription = subscriptionRepositoryFacade.save(
                new Subscription(email));

            newsLetterService.sendSubscribedNotification(newSubscription);

            log.info(StructuredLog.event(
                    "SUBSCRIPTION_CREATED",
                    "구독 정보가 생성되었습니다.",
                    "CREATED"
                )
                .field("subscriptionId", newSubscription.getId())
                .field("status", newSubscription.getSubscriptionStatus())
                .build());
            return;
        }

        if (subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE) {
            return;
        }

        subscription.setSubscribedAt(LocalDateTime.now());
        subscription.setSubscriptionStatus(SubscriptionStatus.SUBSCRIBE);
        subscription.setUnsubscribedAt(null);

        newsLetterService.sendSubscribedNotification(subscription);

        log.info(StructuredLog.event(
                "SUBSCRIPTION_RESUBSCRIBED",
                "구독 정보가 구독 상태로 전환되었습니다.",
                "SUBSCRIBED"
            )
            .field("subscriptionId", subscription.getId())
            .field("userId", subscription.getUser() == null ? null : subscription.getUser().getId())
            .field("status", subscription.getSubscriptionStatus())
            .build());
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

        log.info(StructuredLog.event(
                "SUBSCRIPTION_UNSUBSCRIBED",
                "구독 정보가 구독 해지 상태로 전환되었습니다.",
                "UNSUBSCRIBED"
            )
            .field("subscriptionId", subscription.getId())
            .field("userId", subscription.getUser() == null ? null : subscription.getUser().getId())
            .field("status", subscription.getSubscriptionStatus())
            .build());
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

    public void publishAnnouncementEvent(SubscriptionAnnounceRequest request) {
        publisher.publishEvent(
            new AnnouncementRequestedEvent(request.title(), request.content())
        );

        log.info(StructuredLog.event(
                "ANNOUNCEMENT_DELIVERY_ACCEPTED",
                "공지 메일 발송이 비동기 처리 대상으로 접수되었습니다.",
                "ACCEPTED"
            )
            .build());
    }
}
