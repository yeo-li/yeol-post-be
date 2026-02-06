package com.yeo_li.yeol_post.domain.subscription.service;

import com.yeo_li.yeol_post.domain.post.dto.PostMailCommand;
import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsLetterService {

    private final MailService mailService;

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
        String html = StreamUtils.copyToString(
                new ClassPathResource("mail/post.html").getInputStream(),
                StandardCharsets.UTF_8
            ).replace("{title}", command.title())
            .replace("{summary}", command.summary() != null ? command.summary() : "")
            .replace("{postId}", command.postId().toString())
            .replace("{frontendOrigin}", frontendOrigin)
            .replace("{token}", subscription.getVerifyToken());

        mailService.sendHtmlMail(
            subscription.getEmail(),
            "[yeolpost] 새 글이 올라왔어요!",
            html
        );
    }

    public void sendSubscribedNotification(Subscription subscription) {
        try {

            String html = StreamUtils.copyToString(
                    new ClassPathResource("mail/subscribed.html").getInputStream(),
                    StandardCharsets.UTF_8
                )
                .replace("{frontendOrigin}", frontendOrigin)
                .replace("{token}", subscription.getVerifyToken());

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

            String html = StreamUtils.copyToString(
                    new ClassPathResource("mail/unsubscribed.html").getInputStream(),
                    StandardCharsets.UTF_8
                )
                .replace("{frontendOrigin}", frontendOrigin);

            mailService.sendHtmlMail(
                subscription.getEmail(),
                "[yeolpost] 구독이 해지되었습니다.",
                html
            );
        } catch (IOException e) {
            log.error("{}발송 실패", subscription.getEmail(), e);
        }
    }
}
