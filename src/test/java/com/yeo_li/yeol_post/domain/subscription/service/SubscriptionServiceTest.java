package com.yeo_li.yeol_post.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.yeo_li.yeol_post.domain.subscription.dto.request.SubscriptionAnnounceRequest;
import com.yeo_li.yeol_post.domain.subscription.event.AnnouncementRequestedEvent;
import com.yeo_li.yeol_post.domain.subscription.facade.SubscriptionRepositoryFacade;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepositoryFacade subscriptionRepositoryFacade;

    @Mock
    private NewsLetterService newsLetterService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Nested
    class PublishAnnouncementEventTest {

        @Test
        void publishAnnouncementEvent_공지발송요청이_주어지면_동일한공지이벤트를_발행한다() {
            // given
            SubscriptionAnnounceRequest request = new SubscriptionAnnounceRequest(
                "서비스 점검 안내",
                "<h2>점검 일정</h2><p>2026-02-20 02:00~04:00</p>"
            );

            // when
            subscriptionService.publishAnnouncementEvent(request);

            // then
            ArgumentCaptor<AnnouncementRequestedEvent> eventCaptor =
                ArgumentCaptor.forClass(AnnouncementRequestedEvent.class);

            verify(publisher).publishEvent(eventCaptor.capture());

            AnnouncementRequestedEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.title()).isEqualTo("서비스 점검 안내");
            assertThat(capturedEvent.content())
                .isEqualTo("<h2>점검 일정</h2><p>2026-02-20 02:00~04:00</p>");
        }
    }
}
