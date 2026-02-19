package com.yeo_li.yeol_post.domain.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.like.domain.Like;
import com.yeo_li.yeol_post.domain.like.dto.LikeResponse;
import com.yeo_li.yeol_post.domain.like.exception.LikeExceptionType;
import com.yeo_li.yeol_post.domain.like.repository.LikeRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.Role;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private LikeService likeService;

    @Nested
    class GetLikesTest {

        @Test
        void getLikes_사용자가_존재하지_않으면_사용자없음_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-1"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-1")).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> likeService.getLikes(principal, 1L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(LikeExceptionType.LIKE_USER_NOT_FOUND));
        }

        @Test
        void getLikes_사용자가_존재하면_좋아요여부와_좋아요개수를_반환한다() {
            // given
            User user = createUser(10L, "kakao-like-2");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-2"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-2")).thenReturn(user);
            when(likeRepository.existsLikesByPostIdAndUserId(99L, 10L)).thenReturn(true);
            when(likeRepository.countLikeByPostId(99L)).thenReturn(5);

            // when
            LikeResponse response = likeService.getLikes(principal, 99L);

            // then
            assertThat(response.isLiked()).isTrue();
            assertThat(response.likeCount()).isEqualTo(5);
        }
    }

    @Nested
    class LikeTest {

        @Test
        void like_사용자가_존재하지_않으면_사용자없음_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-3"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-3")).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> likeService.like(principal, 1L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(LikeExceptionType.LIKE_USER_NOT_FOUND));
            verify(likeRepository, never()).save(any(Like.class));
        }

        @Test
        void like_이미_좋아요한_게시물이면_좋아요를_저장하지_않는다() {
            // given
            User user = createUser(11L, "kakao-like-4");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-4"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-4")).thenReturn(user);
            when(likeRepository.existsLikesByPostIdAndUserId(2L, 11L)).thenReturn(true);

            // when
            likeService.like(principal, 2L);

            // then
            verify(likeRepository, never()).save(any(Like.class));
            verifyNoInteractions(postRepository);
        }

        @Test
        void like_좋아요하지_않은_게시물이면_좋아요를_저장한다() {
            // given
            User user = createUser(12L, "kakao-like-5");
            Post post = createPost(3L);
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-5"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-5")).thenReturn(user);
            when(likeRepository.existsLikesByPostIdAndUserId(3L, 12L)).thenReturn(false);
            when(postRepository.findPostById(3L)).thenReturn(post);

            // when
            likeService.like(principal, 3L);

            // then
            ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
            verify(likeRepository).save(captor.capture());
            assertThat(captor.getValue().getUser()).isEqualTo(user);
            assertThat(captor.getValue().getPost()).isEqualTo(post);
        }

        @Test
        void like_게시물이_존재하지_않으면_게시물없음_예외를_발생시킨다() {
            // given
            User user = createUser(13L, "kakao-like-6");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-6"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-6")).thenReturn(user);
            when(likeRepository.existsLikesByPostIdAndUserId(4L, 13L)).thenReturn(false);
            when(postRepository.findPostById(4L)).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> likeService.like(principal, 4L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(LikeExceptionType.LIKE_POST_NOT_FOUND));
            verify(likeRepository, never()).save(any(Like.class));
        }
    }

    @Nested
    class UnlikeTest {

        @Test
        void unlike_사용자가_존재하지_않으면_사용자없음_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-7"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-7")).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> likeService.unlike(principal, 5L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(LikeExceptionType.LIKE_USER_NOT_FOUND));
            verify(likeRepository, never()).deleteLikeByPostIdAndUserId(anyLong(), anyLong());
        }

        @Test
        void unlike_좋아요하지_않은_게시물이면_삭제하지_않는다() {
            // given
            User user = createUser(14L, "kakao-like-8");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-8"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-8")).thenReturn(user);
            when(likeRepository.existsLikesByPostIdAndUserId(6L, 14L)).thenReturn(false);

            // when
            likeService.unlike(principal, 6L);

            // then
            verify(likeRepository, never()).deleteLikeByPostIdAndUserId(6L, 14L);
            verifyNoInteractions(postRepository);
        }

        @Test
        void unlike_이미_좋아요한_게시물이면_좋아요를_삭제한다() {
            // given
            User user = createUser(15L, "kakao-like-9");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-like-9"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-like-9")).thenReturn(user);
            when(likeRepository.existsLikesByPostIdAndUserId(7L, 15L)).thenReturn(true);

            // when
            likeService.unlike(principal, 7L);

            // then
            verify(likeRepository).deleteLikeByPostIdAndUserId(7L, 15L);
            verifyNoInteractions(postRepository);
        }
    }

    private User createUser(Long id, String kakaoId) {
        User user = new User();
        user.setId(id);
        user.setKakaoId(kakaoId);
        user.setName("tester");
        user.setRole(Role.USER);
        return user;
    }

    private Post createPost(Long postId) {
        Post post = new Post();
        post.setId(postId);
        post.setTitle("title");
        post.setContent("content");
        post.setIsPublished(true);
        post.setIsDeleted(false);
        return post;
    }
}
