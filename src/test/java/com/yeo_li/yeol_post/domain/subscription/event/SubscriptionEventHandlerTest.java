package com.yeo_li.yeol_post.domain.subscription.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.subscription.command.AnnouncementMailCommand;
import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.service.NewsLetterService;
import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionEventHandlerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private NewsLetterService newsLetterService;

    @InjectMocks
    private SubscriptionEventHandler subscriptionEventHandler;

    @Nested
    class HandleTest {

        @Test
        void handle_공지요청이벤트가_발생하면_구독자들에게_공지메일발송을_요청한다() {
            // given
            List<Subscription> subscriptions = List.of(
                new Subscription("first@test.com"),
                new Subscription("second@test.com")
            );
            when(subscriptionService.getSubscribedEmail()).thenReturn(subscriptions);

            AnnouncementRequestedEvent event = new AnnouncementRequestedEvent(
                "서비스 점검 안내",
                "<p>점검 공지 본문</p>"
            );

            // when
            subscriptionEventHandler.handle(event);

            // then
            ArgumentCaptor<AnnouncementMailCommand> commandCaptor =
                ArgumentCaptor.forClass(AnnouncementMailCommand.class);

            verify(newsLetterService).sendAnnouncements(eq(subscriptions), commandCaptor.capture());
            assertThat(commandCaptor.getValue().title()).isEqualTo("서비스 점검 안내");
            assertThat(commandCaptor.getValue().content()).isEqualTo("<p>점검 공지 본문</p>");
        }
    }
}
