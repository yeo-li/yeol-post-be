package com.yeo_li.yeol_post.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.comment.service.CommentService;
import com.yeo_li.yeol_post.domain.post.dto.PostCommandFactory;
import com.yeo_li.yeol_post.domain.post.dto.response.PostResponse;
import com.yeo_li.yeol_post.domain.post.service.PostService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private PostCommandFactory postCommandFactory;

    @InjectMocks
    private PostController postController;

    @Test
    void 비관리자_기본목록조회는_발행게시물만_반환한다() {
        List<PostResponse> publishedPosts = List.of();
        when(postService.getAllPublishedPosts()).thenReturn(publishedPosts);

        ResponseEntity<ApiResponse<List<PostResponse>>> response = postController.getPostsByQueryString(
            Map.of(), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResult()).isEqualTo(publishedPosts);
        verify(postService).getAllPublishedPosts();
        verify(postService, never()).getAllPosts();
    }

    @Test
    void 비관리자의_미발행목록요청은_발행게시물만_반환한다() {
        List<PostResponse> publishedPosts = List.of();
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(
            "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(postService.getAllPublishedPosts()).thenReturn(publishedPosts);

        ResponseEntity<ApiResponse<List<PostResponse>>> response = postController.getPostsByQueryString(
            Map.of("is_published", "false"), userAuthentication);

        assertThat(response.getBody().getResult()).isEqualTo(publishedPosts);
        verify(postService).getAllPublishedPosts();
        verify(postService, never()).getAllDraftPosts();
    }

    @Test
    void 관리자의_기본목록조회는_미발행게시물을_포함한다() {
        List<PostResponse> allPosts = List.of();
        when(postService.getAllPosts()).thenReturn(allPosts);

        ResponseEntity<ApiResponse<List<PostResponse>>> response = postController.getPostsByQueryString(
            Map.of(), adminAuthentication());

        assertThat(response.getBody().getResult()).isEqualTo(allPosts);
        verify(postService).getAllPosts();
        verify(postService, never()).getAllPublishedPosts();
    }

    @Test
    void 관리자는_미발행목록을_조회할_수_있다() {
        List<PostResponse> draftPosts = List.of();
        when(postService.getAllDraftPosts()).thenReturn(draftPosts);

        ResponseEntity<ApiResponse<List<PostResponse>>> response = postController.getPostsByQueryString(
            Map.of("is_published", "false"), adminAuthentication());

        assertThat(response.getBody().getResult()).isEqualTo(draftPosts);
        verify(postService).getAllDraftPosts();
        verify(postService, never()).getAllPublishedPosts();
    }

    @Test
    void 비관리자의_미발행최근목록요청은_발행게시물로_제한한다() {
        List<PostResponse> publishedPosts = List.of();
        when(postService.getPostRecent(10, true)).thenReturn(publishedPosts);

        ResponseEntity<ApiResponse<List<PostResponse>>> response = postController.getPostsByQueryString(
            Map.of("limit", "10", "is_published", "false"), null);

        assertThat(response.getBody().getResult()).isEqualTo(publishedPosts);
        verify(postService).getPostRecent(10, true);
    }

    private Authentication adminAuthentication() {
        return new UsernamePasswordAuthenticationToken(
            "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
