package com.yeo_li.yeol_post.domain.comment.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.comment.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @Test
    void 익명댓글닉네임을_생성해_반환한다() {
        when(commentService.generateAnonymousNickname()).thenReturn("포근한토끼");

        var response = commentController.generateAnonymousNickname();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult().nickname()).isEqualTo("포근한토끼");
        verify(commentService).generateAnonymousNickname();
    }
}
