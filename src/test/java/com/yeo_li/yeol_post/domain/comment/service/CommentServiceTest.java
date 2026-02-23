package com.yeo_li.yeol_post.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.comment.domain.Comment;
import com.yeo_li.yeol_post.domain.comment.domain.CommentLike;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentUpdateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentResponse;
import com.yeo_li.yeol_post.domain.comment.exception.CommentExceptionType;
import com.yeo_li.yeol_post.domain.comment.repository.CommentLikeRepository;
import com.yeo_li.yeol_post.domain.comment.repository.CommentRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.Role;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.util.List;
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
    private CommentLikeRepository commentLikeRepository;

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

    @Nested
    class DeleteCommentTest {

        @Test
        void 삭제한다_deleteComment_댓글소유자면_deletedAt을_설정한다() {
            // given
            User owner = createUser(1L);
            Comment comment = createComment(101L, "삭제 대상", owner);
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findByIdAndDeletedAtIsNull(101L)).thenReturn(Optional.of(comment));

            // when
            commentService.deleteComment(principal, 101L);

            // then
            assertThat(comment.getDeletedAt()).isNotNull();
        }

        @Test
        void 발생시킨다_deleteComment_principal에_userId가_없으면_인증실패_예외를() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(principal, 101L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_ID_INVALID));
            verify(commentRepository, never()).findByIdAndDeletedAtIsNull(any());
        }

        @Test
        void 발생시킨다_deleteComment_댓글이_없으면_댓글없음_예외를() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findByIdAndDeletedAtIsNull(101L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(principal, 101L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_NOT_FOUND));
        }

        @Test
        void 발생시킨다_deleteComment_댓글소유자가_아니면_권한없음_예외를() {
            // given
            User owner = createUser(2L);
            Comment comment = createComment(101L, "삭제 대상", owner);
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findByIdAndDeletedAtIsNull(101L)).thenReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(principal, 101L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_FORBIDDEN));
        }
    }

    @Nested
    class UpdateCommentTest {

        @Test
        void 수정한다_updateComment_댓글소유자면_내용을_변경한다() {
            // given
            User owner = createUser(1L);
            Comment comment = createComment(201L, "기존 댓글", owner);
            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findByIdAndDeletedAtIsNull(201L)).thenReturn(Optional.of(comment));

            // when
            commentService.updateComment(principal, 201L, request);

            // then
            assertThat(comment.getContent()).isEqualTo("수정된 댓글");
        }

        @Test
        void 발생시킨다_updateComment_principal에_userId가_없으면_인증실패_예외를() {
            // given
            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(principal, 201L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_ID_INVALID));
            verify(commentRepository, never()).findByIdAndDeletedAtIsNull(any());
        }

        @Test
        void 발생시킨다_updateComment_댓글이_없으면_댓글없음_예외를() {
            // given
            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findByIdAndDeletedAtIsNull(201L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(principal, 201L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_NOT_FOUND));
        }

        @Test
        void 발생시킨다_updateComment_댓글소유자가_아니면_권한없음_예외를() {
            // given
            User owner = createUser(2L);
            Comment comment = createComment(201L, "기존 댓글", owner);
            CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findByIdAndDeletedAtIsNull(201L)).thenReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> commentService.updateComment(principal, 201L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_FORBIDDEN));
        }
    }

    @Nested
    class SaveReplyTest {

        @Test
        void 저장한다_saveReply_유효한요청이면_답글을_저장하고_응답을_반환한다() {
            // given
            User user = createUser(1L);
            Post post = createPost(10L);
            Comment parent = createComment(301L, "부모 댓글", createUser(2L));
            parent.setPost(post);
            CommentCreateRequest request = new CommentCreateRequest("답글 본문");

            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(301L)).thenReturn(Optional.of(parent));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
                Comment reply = invocation.getArgument(0);
                reply.setId(302L);
                return reply;
            });

            // when
            var response = commentService.saveReply(principal, 301L, request);

            // then
            ArgumentCaptor<Comment> replyCaptor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository).save(replyCaptor.capture());
            Comment savedReply = replyCaptor.getValue();

            assertThat(savedReply.getParentComment()).isEqualTo(parent);
            assertThat(savedReply.getPost()).isEqualTo(post);
            assertThat(savedReply.getUser()).isEqualTo(user);
            assertThat(savedReply.getContent()).isEqualTo("답글 본문");

            assertThat(response.commentId()).isEqualTo(302L);
            assertThat(response.userNickname()).isEqualTo(user.getNickname());
            assertThat(response.content()).isEqualTo("답글 본문");
            assertThat(response.likeCount()).isZero();
            assertThat(response.isLiked()).isFalse();
            assertThat(response.isOwner()).isTrue();
        }

        @Test
        void 발생시킨다_saveReply_principal에_userId가_없으면_인증실패_예외를() {
            // given
            CommentCreateRequest request = new CommentCreateRequest("답글 본문");
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> commentService.saveReply(principal, 301L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_ID_INVALID));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        void 발생시킨다_saveReply_사용자가_존재하지_않으면_사용자없음_예외를() {
            // given
            CommentCreateRequest request = new CommentCreateRequest("답글 본문");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.saveReply(principal, 301L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_NOT_FOUND));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        void 발생시킨다_saveReply_부모댓글이_없으면_댓글없음_예외를() {
            // given
            User user = createUser(1L);
            CommentCreateRequest request = new CommentCreateRequest("답글 본문");
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(301L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.saveReply(principal, 301L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_NOT_FOUND));
            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    class GetCommentsTest {

        @Test
        void 반환한다_getComments_댓글과답글의_좋아요수와좋아요여부를_반환한다() {
            // given
            Post post = createPost(10L);
            User commentWriter = createUser(2L);
            User replyWriter = createUser(3L);

            Comment comment = createComment(501L, "댓글", commentWriter);
            comment.setPost(post);

            Comment reply = createComment(502L, "답글", replyWriter);
            reply.setPost(post);
            reply.setParentComment(comment);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(commentRepository.findCommentsByPostIdAndParentCommentIsNull(10L))
                .thenReturn(List.of(comment));
            when(commentRepository.findCommentsByParentComment(comment))
                .thenReturn(List.of(reply));

            when(commentLikeRepository.countByCommentId(501L)).thenReturn(3L);
            when(commentLikeRepository.existsByCommentIdAndUserId(501L, 1L)).thenReturn(true);

            when(commentLikeRepository.countByCommentId(502L)).thenReturn(1L);
            when(commentLikeRepository.existsByCommentIdAndUserId(502L, 1L)).thenReturn(false);

            // when
            var result = commentService.getComments(principal, 10L);

            // then
            assertThat(result.comments()).hasSize(1);

            var responseComment = result.comments().get(0);
            assertThat(responseComment.commentId()).isEqualTo(501L);
            assertThat(responseComment.likeCount()).isEqualTo(3);
            assertThat(responseComment.isLiked()).isTrue();
            assertThat(responseComment.isOwner()).isFalse();
            assertThat(responseComment.replies()).hasSize(1);

            var responseReply = responseComment.replies().get(0);
            assertThat(responseReply.commentId()).isEqualTo(502L);
            assertThat(responseReply.likeCount()).isEqualTo(1);
            assertThat(responseReply.isLiked()).isFalse();
            assertThat(responseReply.isOwner()).isFalse();
        }
    }

    @Nested
    class LikeCommentTest {

        @Test
        void 저장한다_likeComment_유효한요청이면_댓글좋아요를_저장한다() {
            // given
            User user = createUser(1L);
            Comment comment = createComment(401L, "댓글", createUser(2L));
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(401L)).thenReturn(Optional.of(comment));
            when(commentLikeRepository.existsByCommentIdAndUserId(401L, 1L)).thenReturn(false);

            // when
            commentService.likeComment(principal, 401L);

            // then
            ArgumentCaptor<CommentLike> captor = ArgumentCaptor.forClass(CommentLike.class);
            verify(commentLikeRepository).save(captor.capture());
            assertThat(captor.getValue().getUser()).isEqualTo(user);
            assertThat(captor.getValue().getComment()).isEqualTo(comment);
        }

        @Test
        void 저장하지않는다_likeComment_이미좋아요한댓글이면_저장하지않는다() {
            // given
            User user = createUser(1L);
            Comment comment = createComment(401L, "댓글", createUser(2L));
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(401L)).thenReturn(Optional.of(comment));
            when(commentLikeRepository.existsByCommentIdAndUserId(401L, 1L)).thenReturn(true);

            // when
            commentService.likeComment(principal, 401L);

            // then
            verify(commentLikeRepository, never()).save(any(CommentLike.class));
        }

        @Test
        void 발생시킨다_likeComment_principal에userId가없으면_인증실패예외를() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> commentService.likeComment(principal, 401L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_ID_INVALID));
            verify(commentLikeRepository, never()).save(any(CommentLike.class));
        }

        @Test
        void 발생시킨다_likeComment_사용자가없으면_사용자없음예외를() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.likeComment(principal, 401L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_NOT_FOUND));
            verify(commentLikeRepository, never()).save(any(CommentLike.class));
        }

        @Test
        void 발생시킨다_likeComment_댓글이없으면_댓글없음예외를() {
            // given
            User user = createUser(1L);
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(401L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.likeComment(principal, 401L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_NOT_FOUND));
            verify(commentLikeRepository, never()).save(any(CommentLike.class));
        }
    }

    @Nested
    class UnlikeCommentTest {

        @Test
        void 삭제한다_unlikeComment_이미좋아요한댓글이면_좋아요를삭제한다() {
            // given
            User user = createUser(1L);
            Comment comment = createComment(402L, "댓글", createUser(2L));
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(402L)).thenReturn(Optional.of(comment));
            when(commentLikeRepository.existsByCommentIdAndUserId(402L, 1L)).thenReturn(true);

            // when
            commentService.unlikeComment(principal, 402L);

            // then
            verify(commentLikeRepository).deleteByCommentIdAndUserId(402L, 1L);
        }

        @Test
        void 삭제하지않는다_unlikeComment_좋아요하지않은댓글이면_삭제하지않는다() {
            // given
            User user = createUser(1L);
            Comment comment = createComment(402L, "댓글", createUser(2L));
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(402L)).thenReturn(Optional.of(comment));
            when(commentLikeRepository.existsByCommentIdAndUserId(402L, 1L)).thenReturn(false);

            // when
            commentService.unlikeComment(principal, 402L);

            // then
            verify(commentLikeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
        }

        @Test
        void 발생시킨다_unlikeComment_principal에userId가없으면_인증실패예외를() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> commentService.unlikeComment(principal, 402L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_ID_INVALID));
            verify(commentLikeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
        }

        @Test
        void 발생시킨다_unlikeComment_사용자가없으면_사용자없음예외를() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.unlikeComment(principal, 402L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_USER_NOT_FOUND));
            verify(commentLikeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
        }

        @Test
        void 발생시킨다_unlikeComment_댓글이없으면_댓글없음예외를() {
            // given
            User user = createUser(1L);
            when(principal.getAttributes()).thenReturn(Map.of("userId", 1L));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(commentRepository.findByIdAndDeletedAtIsNull(402L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.unlikeComment(principal, 402L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(CommentExceptionType.COMMENT_NOT_FOUND));
            verify(commentLikeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
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

    private Comment createComment(Long commentId, String content, User user) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent(content);
        comment.setUser(user);
        return comment;
    }
}
