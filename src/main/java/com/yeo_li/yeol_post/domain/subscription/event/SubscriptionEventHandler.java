package com.yeo_li.yeol_post.domain.subscription.event;

import com.yeo_li.yeol_post.domain.comment.event.CommentLikedEvent;
import com.yeo_li.yeol_post.domain.comment.event.ReplyCreatedEvent;
import com.yeo_li.yeol_post.domain.like.event.PostLikedEvent;
import com.yeo_li.yeol_post.domain.post.dto.command.PostMailCommand;
import com.yeo_li.yeol_post.domain.post.event.CommentCreatedEvent;
import com.yeo_li.yeol_post.domain.post.event.PostPublishedEvent;
import com.yeo_li.yeol_post.domain.subscription.command.AnnouncementMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.CommentLikeMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.CommentMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.PostLikeMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.ReplyMailCommand;
import com.yeo_li.yeol_post.domain.subscription.service.NewsLetterService;
import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
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

    @Async
    @EventListener
    public void handle(AnnouncementRequestedEvent event) {
        newsLetterService.sendAnnouncements(subscriptionService.getSubscribedEmail(),
            new AnnouncementMailCommand(event.title(), event.content()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentCreatedEvent event) {
        newsLetterService.sendCommentNotification(CommentMailCommand.from(event));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReplyCreatedEvent event) {
        newsLetterService.sendReplyNotification(ReplyMailCommand.from(event));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostLikedEvent event) {
        newsLetterService.sendPostLikeNotification(PostLikeMailCommand.from(event));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentLikedEvent event) {
        newsLetterService.sendCommentLikeNotification(CommentLikeMailCommand.from(event));
    }

}
