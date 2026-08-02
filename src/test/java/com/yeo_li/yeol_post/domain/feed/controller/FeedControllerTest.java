package com.yeo_li.yeol_post.domain.feed.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.yeo_li.yeol_post.domain.feed.service.FeedService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private FeedController feedController;

    @Nested
    class LikeFeedTest {

        @Test
        void likeFeed_피드아이디가_주어지면_좋아요를_요청하고_성공응답을_반환한다() {
            // when
            ResponseEntity<ApiResponse<Void>> response = feedController.likeFeed(principal, 100L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getIsSuccess()).isTrue();
            verify(feedService).likeFeed(principal, 100L);
        }
    }

    @Nested
    class UnlikeFeedTest {

        @Test
        void unlikeFeed_피드아이디가_주어지면_좋아요취소를_요청하고_성공응답을_반환한다() {
            // when
            ResponseEntity<ApiResponse<Void>> response = feedController.unlikeFeed(principal, 100L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getIsSuccess()).isTrue();
            verify(feedService).unlikeFeed(principal, 100L);
        }
    }
}
