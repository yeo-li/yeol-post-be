package com.yeo_li.yeol_post.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.comment.domain.Comment;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentResponse;
import com.yeo_li.yeol_post.domain.comment.exception.CommentExceptionType;
import com.yeo_li.yeol_post.domain.comment.repository.CommentRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.Role;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private CommentService commentService;

    @Nested
    class SaveCommentTest {

        @Test
        void saveComment_유효한요청이면_댓글을_저장하고_응답을_반환한다() {
            // given
            User user = createUser(1L);
            Post post = createPost(10L);
            CommentCreateRequest request = new CommentCreateRequest("댓글 본문");

            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findPostById(10L)).thenReturn(post);
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
                Comment comment = invocation.getArgument(0);
                comment.setId(100L);
                return comment;
            });

            // when
            CommentResponse response = commentService.saveComment(principal, 10L, request);

            // then
            ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository).save(commentCaptor.capture());

            Comment savedComment = commentCaptor.getValue();
            assertThat(savedComment.getContent()).isEqualTo("댓글 본문");
            assertThat(savedComment.getPost()).isEqualTo(post);
            assertThat(savedComment.getUser()).isEqualTo(user);
            assertThat(savedComment.getParentComment()).isNull();

            assertThat(response.commentId()).isEqualTo(100L);
            assertThat(response.userNickname()).isEqualTo(user.getNickname());
            assertThat(response.content()).isEqualTo("댓글 본문");
            assertThat(response.likeCount()).isZero();
            assertThat(response.isLiked()).isFalse();
            assertThat(response.isOwner()).isTrue();
            assertThat(response.replies()).isEmpty();
        }

        @Test
        void saveComment_principal에_userId가_없으면_인증실패_예외를_발생시킨다() {
            // given
            CommentCreateRequest request = new CommentCreateRequest("댓글 본문");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-id-only"));

            // when & then
            assertThatThrownBy(() -> commentService.saveComment(principal, 10L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_ID_INVALID));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        void saveComment_사용자가_존재하지_않으면_사용자없음_예외를_발생시킨다() {
            // given
            CommentCreateRequest request = new CommentCreateRequest("댓글 본문");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 2L));
            when(userRepository.findById(2L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.saveComment(principal, 10L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_NOT_FOUND));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        void saveComment_게시물이_존재하지_않으면_게시물없음_예외를_발생시킨다() {
            // given
            User user = createUser(1L);
            CommentCreateRequest request = new CommentCreateRequest("댓글 본문");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(postRepository.findPostById(999L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> commentService.saveComment(principal, 999L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_POST_NOT_FOUND));
            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setKakaoId("kakao-" + userId);
        user.setName("사용자" + userId);
        user.setNickname("닉네임" + userId);
        user.setRole(Role.USER);
        return user;
    }

    private Post createPost(Long postId) {
        Post post = new Post();
        post.setId(postId);
        post.setTitle("게시물 제목");
        post.setContent("게시물 본문");
        post.setIsPublished(true);
        post.setIsDeleted(false);
        return post;
    }
}
