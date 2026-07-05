package com.yeo_li.yeol_post.domain.subscription.service;

import com.yeo_li.yeol_post.domain.post.dto.command.PostMailCommand;
import com.yeo_li.yeol_post.domain.subscription.command.AnnouncementMailCommand;
import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.dto.command.CommentMailCommand;
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
                log.error("{}발송 실패", subscription.getEmail(), e);
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
    }

    public void sendAnnouncements(List<Subscription> subscriptions,
        AnnouncementMailCommand command) {
        for (Subscription subscription : subscriptions) {
            try {
                sendAnnouncement(subscription, command);
            } catch (IOException | IllegalStateException e) {
                log.error("{}발송 실패", subscription.getEmail(), e);
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
        } catch (IOException e) {
            log.error("{}발송 실패", subscription.getEmail(), e);
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
        } catch (IOException e) {
            log.error("{}발송 실패", subscription.getEmail(), e);
        }
    }

    public void sendCommentNotification(CommentMailCommand command) {
        if (command.receiverEmail() == null || command.receiverEmail().isBlank()) {
            log.warn("댓글 알림 메일 수신자 이메일이 없어 발송하지 않습니다. postId={}, commentId={}",
                command.postId(), command.commentId());
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
        } catch (IOException e) {
            log.error("{} 댓글 알림 메일 발송 실패", command.receiverEmail(), e);
        }
    }
}
