package com.yeo_li.yeol_post.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.yeo_li.yeol_post.domain.subscription.command.AnnouncementMailCommand;
import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NewsLetterServiceTest {

    @Mock
    private MailService mailService;

    @InjectMocks
    private NewsLetterService newsLetterService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(newsLetterService, "frontendOrigin", "https://yeolpost.dev");
    }

    @Nested
    class SendAnnouncementTest {

        @Test
        void sendAnnouncement_공지요청이_주어지면_치환된HTML메일을_발송한다() throws IOException {
            // given
            Subscription subscription = new Subscription("announce@test.com");
            subscription.setVerifyToken("verify-token-123");
            AnnouncementMailCommand command = new AnnouncementMailCommand(
                "서비스 점검 안내",
                "<h2>점검 일정</h2><p>2026-02-20 02:00~04:00</p>"
            );

            // when
            newsLetterService.sendAnnouncement(subscription, command);

            // then
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

            verify(mailService).sendHtmlMail(eq("announce@test.com"), subjectCaptor.capture(),
                htmlCaptor.capture());

            String subject = subjectCaptor.getValue();
            String html = htmlCaptor.getValue();

            assertThat(subject).isEqualTo("[공지] 서비스 점검 안내");
            assertThat(html).contains("서비스 점검 안내");
            assertThat(html).contains("<h2>점검 일정</h2><p>2026-02-20 02:00~04:00</p>");
            assertThat(html).contains("https://yeolpost.dev");
            assertThat(html).contains("verify-token-123");
            assertThat(html).doesNotContain("{title}");
            assertThat(html).doesNotContain("{content}");
            assertThat(html).doesNotContain("{frontendOrigin}");
            assertThat(html).doesNotContain("{token}");
        }
    }

    @Nested
    class SendAnnouncementsTest {

        @Test
        void sendAnnouncements_일부발송이_실패해도_다음구독자발송을_계속한다() {
            // given
            Subscription failedSubscription = new Subscription("failed@test.com");
            Subscription successSubscription = new Subscription("success@test.com");

            AnnouncementMailCommand command = new AnnouncementMailCommand(
                "공지 제목",
                "<p>공지 본문</p>"
            );

            doThrow(new IllegalStateException("메일 발송 실패"))
                .when(mailService)
                .sendHtmlMail(eq("failed@test.com"), anyString(), anyString());

            // when & then
            assertThatCode(() ->
                newsLetterService.sendAnnouncements(
                    List.of(failedSubscription, successSubscription),
                    command
                )
            ).doesNotThrowAnyException();

            verify(mailService).sendHtmlMail(eq("failed@test.com"), anyString(), anyString());
            verify(mailService).sendHtmlMail(eq("success@test.com"), anyString(), anyString());
        }
    }
}
