package com.yeo_li.yeol_post.domain.subscription.service;

import com.yeo_li.yeol_post.domain.post.dto.command.PostMailCommand;
import com.yeo_li.yeol_post.domain.subscription.command.AnnouncementMailCommand;
import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.dto.command.CommentLikeMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.CommentMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.PostLikeMailCommand;
import com.yeo_li.yeol_post.domain.subscription.dto.command.ReplyMailCommand;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsLetterService {

    private final MailService mailService;
    private final MailTemplateRenderer mailTemplateRenderer;

    @Value("${app.frontend.origin}")
    private String frontendOrigin;

    public void sendPublishedPostMails(List<Subscription> subscriptions, PostMailCommand command) {

        for (Subscription subscription : subscriptions) {
            try {
                sendPublishedPostMail(subscription, command);
            } catch (IOException | IllegalStateException e) {
                log.error(StructuredLog.event(
                        "PUBLISHED_POST_MAIL_FAILED",
                        "게시물 발행 메일 발송에 실패했습니다.",
                        "MAIL_SEND_FAILED"
                    )
                    .field("subscriptionId", subscription.getId())
                    .field("postId", command.postId())
                    .throwable(e)
                    .build());
            }
        }
    }

    public void sendPublishedPostMail(Subscription subscription, PostMailCommand command)
        throws IOException {
        String html = mailTemplateRenderer.render("mail/post.html", Map.of(
            "title", command.title(),
            "summary", command.summary() != null ? command.summary() : "",
            "postId", command.postId().toString(),
            "frontendOrigin", frontendOrigin,
            "token", subscription.getVerifyToken()
        ));

        mailService.sendHtmlMail(
            subscription.getEmail(),
            "[yeolpost] 새 글이 올라왔어요!",
            html
        );

        log.info(StructuredLog.event(
                "PUBLISHED_POST_MAIL_SENT",
                "게시물 발행 메일이 발송되었습니다.",
                "SENT"
            )
            .field("subscriptionId", subscription.getId())
            .field("postId", command.postId())
            .build());
    }

    public void sendAnnouncements(List<Subscription> subscriptions,
        AnnouncementMailCommand command) {
        for (Subscription subscription : subscriptions) {
            try {
                sendAnnouncement(subscription, command);
            } catch (IOException | IllegalStateException e) {
                log.error(StructuredLog.event(
                        "ANNOUNCEMENT_MAIL_FAILED",
                        "공지 메일 발송에 실패했습니다.",
                        "MAIL_SEND_FAILED"
                    )
                    .field("subscriptionId", subscription.getId())
                    .throwable(e)
                    .build());
            }
        }
    }

    public void sendAnnouncement(Subscription subscription, AnnouncementMailCommand command)
        throws IOException {
        String html = mailTemplateRenderer.render("mail/announcement.html", Map.of(
            "title", command.title(),
            "frontendOrigin", frontendOrigin,
            "token", subscription.getVerifyToken(),
            "content", command.content()
        ));

        mailService.sendHtmlMail(
            subscription.getEmail(),
            "[공지] " + command.title(),
            html
        );

        log.info(StructuredLog.event(
                "ANNOUNCEMENT_MAIL_SENT",
                "공지 메일이 발송되었습니다.",
                "SENT"
            )
            .field("subscriptionId", subscription.getId())
            .build());
    }

    public void sendSubscribedNotification(Subscription subscription) {
        try {

            String html = mailTemplateRenderer.render("mail/subscribed.html", Map.of(
                "frontendOrigin", frontendOrigin,
                "token", subscription.getVerifyToken()
            ));

            mailService.sendHtmlMail(
                subscription.getEmail(),
                "[yeolpost] 구독이 완료되었습니다.",
                html
            );

            log.info(StructuredLog.event(
                    "SUBSCRIBED_NOTIFICATION_SENT",
                    "구독 완료 메일이 발송되었습니다.",
                    "SENT"
                )
                .field("subscriptionId", subscription.getId())
                .field("userId", subscription.getUser() == null ? null : subscription.getUser().getId())
                .build());
        } catch (IOException | IllegalStateException e) {
            log.error(StructuredLog.event(
                    "SUBSCRIBED_NOTIFICATION_FAILED",
                    "구독 완료 메일 발송에 실패했습니다.",
                    "MAIL_SEND_FAILED"
                )
                .field("subscriptionId", subscription.getId())
                .field("userId", subscription.getUser() == null ? null : subscription.getUser().getId())
                .throwable(e)
                .build());
        }
    }

    public void sendUnsubscribedNotification(Subscription subscription) {
        try {

            String html = mailTemplateRenderer.render("mail/unsubscribed.html", Map.of(
                "frontendOrigin", frontendOrigin
            ));

            mailService.sendHtmlMail(
                subscription.getEmail(),
                "[yeolpost] 구독이 해지되었습니다.",
                html
            );

            log.info(StructuredLog.event(
                    "UNSUBSCRIBED_NOTIFICATION_SENT",
                    "구독 해지 메일이 발송되었습니다.",
                    "SENT"
                )
                .field("subscriptionId", subscription.getId())
                .field("userId", subscription.getUser() == null ? null : subscription.getUser().getId())
                .build());
        } catch (IOException | IllegalStateException e) {
            log.error(StructuredLog.event(
                    "UNSUBSCRIBED_NOTIFICATION_FAILED",
                    "구독 해지 메일 발송에 실패했습니다.",
                    "MAIL_SEND_FAILED"
                )
                .field("subscriptionId", subscription.getId())
                .field("userId", subscription.getUser() == null ? null : subscription.getUser().getId())
                .throwable(e)
                .build());
        }
    }

    public void sendCommentNotification(CommentMailCommand command) {
        if (command.receiverEmail() == null || command.receiverEmail().isBlank()) {
            log.warn(StructuredLog.event(
                    "COMMENT_NOTIFICATION_SKIPPED",
                    "댓글 알림 메일 수신자 이메일이 없어 발송하지 않습니다.",
                    "RECEIVER_EMAIL_MISSING"
                )
                .field("postId", command.postId())
                .field("commentId", command.commentId())
                .field("commentAuthorUserId", command.commentAuthorUserId())
                .build());
            return;
        }

        try {
            String html = mailTemplateRenderer.render("mail/comment-notification.html", Map.of(
                "frontendOrigin", frontendOrigin,
                "postId", command.postId().toString(),
                "commentId", command.commentId().toString(),
                "postTitle", command.postTitle(),
                "commentAuthorNickname", command.commentAuthorNickname(),
                "commentContent", command.commentContent()
            ));

            mailService.sendHtmlMail(
                command.receiverEmail(),
                "[yeolpost] 새 댓글이 달렸어요.",
                html
            );

            log.info(StructuredLog.event(
                    "COMMENT_NOTIFICATION_SENT",
                    "댓글 알림 메일이 발송되었습니다.",
                    "SENT"
                )
                .field("postId", command.postId())
                .field("commentId", command.commentId())
                .field("commentAuthorUserId", command.commentAuthorUserId())
                .build());
        } catch (IOException | IllegalStateException e) {
            log.error(StructuredLog.event(
                    "COMMENT_NOTIFICATION_FAILED",
                    "댓글 알림 메일 발송에 실패했습니다.",
                    "MAIL_SEND_FAILED"
                )
                .field("postId", command.postId())
                .field("commentId", command.commentId())
                .field("commentAuthorUserId", command.commentAuthorUserId())
                .throwable(e)
                .build());
        }
    }

    public void sendReplyNotification(ReplyMailCommand command) {
        if (command.receiverEmail() == null || command.receiverEmail().isBlank()) {
            log.warn(StructuredLog.event(
                    "REPLY_NOTIFICATION_SKIPPED",
                    "답글 알림 메일 수신자 이메일이 없어 발송하지 않습니다.",
                    "RECEIVER_EMAIL_MISSING"
                )
                .field("postId", command.postId())
                .field("replyId", command.replyId())
                .field("replyAuthorUserId", command.replyAuthorUserId())
                .build());
            return;
        }

        try {
            String html = mailTemplateRenderer.render("mail/reply-notification.html", Map.of(
                "frontendOrigin", frontendOrigin,
                "postId", command.postId().toString(),
                "replyId", command.replyId().toString(),
                "postTitle", command.postTitle(),
                "replyAuthorNickname", command.replyAuthorNickname(),
                "replyContent", command.replyContent()
            ));

            mailService.sendHtmlMail(
                command.receiverEmail(),
                "[yeolpost] 새 답글이 달렸어요.",
                html
            );

            log.info(StructuredLog.event(
                    "REPLY_NOTIFICATION_SENT",
                    "답글 알림 메일이 발송되었습니다.",
                    "SENT"
                )
                .field("postId", command.postId())
                .field("replyId", command.replyId())
                .field("replyAuthorUserId", command.replyAuthorUserId())
                .build());
        } catch (IOException | IllegalStateException e) {
            log.error(StructuredLog.event(
                    "REPLY_NOTIFICATION_FAILED",
                    "답글 알림 메일 발송에 실패했습니다.",
                    "MAIL_SEND_FAILED"
                )
                .field("postId", command.postId())
                .field("replyId", command.replyId())
                .field("replyAuthorUserId", command.replyAuthorUserId())
                .throwable(e)
                .build());
        }
    }

    public void sendPostLikeNotification(PostLikeMailCommand command) {
        if (command.receiverEmail() == null || command.receiverEmail().isBlank()) {
            log.warn(StructuredLog.event(
                    "POST_LIKE_NOTIFICATION_SKIPPED",
                    "게시물 좋아요 알림 메일 수신자 이메일이 없어 발송하지 않습니다.",
                    "RECEIVER_EMAIL_MISSING"
                )
                .field("postId", command.postId())
                .field("likerUserId", command.likerUserId())
                .build());
            return;
        }

        try {
            String html = mailTemplateRenderer.render("mail/post-like-notification.html", Map.of(
                "frontendOrigin", frontendOrigin,
                "postId", command.postId().toString(),
                "postTitle", command.postTitle(),
                "likerNickname", command.likerNickname()
            ));

            mailService.sendHtmlMail(
                command.receiverEmail(),
                "[yeolpost] 새 좋아요가 눌렸어요.",
                html
            );

            log.info(StructuredLog.event(
                    "POST_LIKE_NOTIFICATION_SENT",
                    "게시물 좋아요 알림 메일이 발송되었습니다.",
                    "SENT"
                )
                .field("postId", command.postId())
                .field("likerUserId", command.likerUserId())
                .build());
        } catch (IOException | IllegalStateException e) {
            log.error(StructuredLog.event(
                    "POST_LIKE_NOTIFICATION_FAILED",
                    "게시물 좋아요 알림 메일 발송에 실패했습니다.",
                    "MAIL_SEND_FAILED"
                )
                .field("postId", command.postId())
                .field("likerUserId", command.likerUserId())
                .throwable(e)
                .build());
        }
    }

    public void sendCommentLikeNotification(CommentLikeMailCommand command) {
        if (command.receiverEmail() == null || command.receiverEmail().isBlank()) {
            log.warn(StructuredLog.event(
                    "COMMENT_LIKE_NOTIFICATION_SKIPPED",
                    "댓글 좋아요 알림 메일 수신자 이메일이 없어 발송하지 않습니다.",
                    "RECEIVER_EMAIL_MISSING"
                )
                .field("postId", command.postId())
                .field("commentId", command.commentId())
                .field("likerUserId", command.likerUserId())
                .build());
            return;
        }

        try {
            String html = mailTemplateRenderer.render("mail/comment-like-notification.html", Map.of(
                "frontendOrigin", frontendOrigin,
                "postId", command.postId().toString(),
                "commentId", command.commentId().toString(),
                "postTitle", command.postTitle(),
                "likerNickname", command.likerNickname(),
                "commentContent", command.commentContent()
            ));

            mailService.sendHtmlMail(
                command.receiverEmail(),
                "[yeolpost] 댓글에 새 좋아요가 눌렸어요.",
                html
            );

            log.info(StructuredLog.event(
                    "COMMENT_LIKE_NOTIFICATION_SENT",
                    "댓글 좋아요 알림 메일이 발송되었습니다.",
                    "SENT"
                )
                .field("postId", command.postId())
                .field("commentId", command.commentId())
                .field("likerUserId", command.likerUserId())
                .build());
        } catch (IOException | IllegalStateException e) {
            log.error(StructuredLog.event(
                    "COMMENT_LIKE_NOTIFICATION_FAILED",
                    "댓글 좋아요 알림 메일 발송에 실패했습니다.",
                    "MAIL_SEND_FAILED"
                )
                .field("postId", command.postId())
                .field("commentId", command.commentId())
                .field("likerUserId", command.likerUserId())
                .throwable(e)
                .build());
        }
    }
}
