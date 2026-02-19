package com.yeo_li.yeol_post.domain.like.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.like.dto.LikeResponse;
import com.yeo_li.yeol_post.domain.like.service.LikeService;
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
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private LikeController likeController;

    @Nested
    class GetLikesTest {

        @Test
        void getLikes_게시물아이디가_주어지면_좋아요조회결과를_반환한다() {
            // given
            LikeResponse likeResponse = new LikeResponse(true, 7);
            when(likeService.getLikes(principal, 1L)).thenReturn(likeResponse);

            // when
            ResponseEntity<ApiResponse<LikeResponse>> response = likeController.getLikes(principal, 1L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getResult()).isEqualTo(likeResponse);
            verify(likeService).getLikes(principal, 1L);
        }
    }

    @Nested
    class LikeTest {

        @Test
        void like_게시물아이디가_주어지면_좋아요를_요청하고_성공응답을_반환한다() {
            // when
            ResponseEntity<ApiResponse<?>> response = likeController.like(principal, 2L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getIsSuccess()).isTrue();
            verify(likeService).like(principal, 2L);
        }
    }

    @Nested
    class UnlikeTest {

        @Test
        void unlike_게시물아이디가_주어지면_좋아요취소를_요청하고_성공응답을_반환한다() {
            // when
            ResponseEntity<ApiResponse<?>> response = likeController.unlike(principal, 3L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getIsSuccess()).isTrue();
            verify(likeService).unlike(principal, 3L);
        }
    }
}
