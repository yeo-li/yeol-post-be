package com.yeo_li.yeol_post.domain.subscription.event;

import com.yeo_li.yeol_post.domain.post.dto.PostMailCommand;
import com.yeo_li.yeol_post.domain.post.event.PostPublishedEvent;
import com.yeo_li.yeol_post.domain.subscription.service.NewsLetterService;
import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SubscriptionEventHandler {

    private final SubscriptionService subscriptionService;
    private final NewsLetterService newsLetterService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostPublishedEvent event) {
        newsLetterService.sendPublishedPostMails(subscriptionService.getSubscribedEmail(),
            new PostMailCommand(event.postId(), event.title(), event.summary()));
    }
}
