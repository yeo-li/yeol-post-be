package com.yeo_li.yeol_post.domain.comment.service;

import com.yeo_li.yeol_post.domain.comment.domain.Comment;
import com.yeo_li.yeol_post.domain.comment.domain.CommentLike;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentUpdateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentListResponse;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentReplyResponse;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentResponse;
import com.yeo_li.yeol_post.domain.comment.exception.CommentExceptionType;
import com.yeo_li.yeol_post.domain.comment.repository.CommentLikeRepository;
import com.yeo_li.yeol_post.domain.comment.repository.CommentRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse saveComment(OAuth2User principal, Long postId,
        CommentCreateRequest request) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_USER_NOT_FOUND));

        Post post = postRepository.findPostById(postId);
        if (post == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_POST_NOT_FOUND);
        }

        String sanitizedContent = sanitizeCommentContent(request.content());

        Comment savedComment = commentRepository.save(
            new Comment(sanitizedContent, post, user, null)
        );

        return convertCommentResponse(userId, savedComment);
    }

    @Transactional
    public void deleteComment(OAuth2User principal, Long commentId) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new GeneralException(CommentExceptionType.COMMENT_FORBIDDEN);
        }

        comment.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public void updateComment(OAuth2User principal, Long commentId, CommentUpdateRequest request) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new GeneralException(CommentExceptionType.COMMENT_FORBIDDEN);
        }

        comment.setContent(sanitizeCommentContent(request.content()));
    }

    @Transactional
    public CommentReplyResponse saveReply(OAuth2User principal, Long commentId,
        CommentCreateRequest request) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_USER_NOT_FOUND));

        Comment parentComment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_NOT_FOUND));

        String sanitizedContent = sanitizeCommentContent(request.content());

        Comment reply = commentRepository.save(
            new Comment(sanitizedContent, parentComment.getPost(), user, parentComment)
        );

        return convertCommentReplyResponse(userId, reply);
    }

    @Transactional
    public void likeComment(OAuth2User principal, Long commentId) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_USER_NOT_FOUND));

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }

        commentLikeRepository.save(new CommentLike(user, comment));
    }

    @Transactional
    public void unlikeComment(OAuth2User principal, Long commentId) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_USER_NOT_FOUND));

        commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public CommentListResponse getComments(OAuth2User principal, Long postId) {
        Long userId = getUserId(principal);

        // 댓글 가져오기
        List<Comment> comments = commentRepository.findCommentsByPostIdAndParentCommentIsNull(
            postId);

        if (comments.isEmpty() && !postRepository.existsPostById(postId)) {
            throw new GeneralException(CommentExceptionType.COMMENT_POST_NOT_FOUND);
        }

        // CommentResponse 만들기
        // N+1 문제가 있음 근데 일단 경험을 해보기 위해서 그냥 둘거임
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse = convertCommentResponse(userId, comment);

            List<Comment> replies = commentRepository.findCommentsByParentCommentAndDeletedAtIsNull(
                comment);
            for (Comment reply : replies) {
                CommentReplyResponse commentReplyResponse = convertCommentReplyResponse(userId,
                    reply);
                commentResponse.replies().add(commentReplyResponse);
            }
            if (commentResponse.isDeleted() && commentResponse.replies().isEmpty()) {
                continue;
            }
            commentResponses.add(commentResponse);
        }

        return new CommentListResponse(commentResponses);
    }

    private CommentResponse convertCommentResponse(Long userId, Comment comment) {
        int likeCount = Math.toIntExact(commentLikeRepository.countByCommentId(comment.getId()));
        boolean isLiked = userId != null
            && commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId);

        return new CommentResponse(
            comment.getId(),
            comment.getDeletedAt() == null ? comment.getUser().getNickname() : "(알수없음)",
            comment.getDeletedAt() == null ? comment.getContent() : "삭제된 댓글입니다.",
            comment.getCreatedAt(),
            likeCount,
            comment.getDeletedAt() == null ? isLiked : false,
            Objects.equals(comment.getUser().getId(), userId),
            comment.getDeletedAt() != null,
            new ArrayList<>()
        );
    }

    private CommentReplyResponse convertCommentReplyResponse(Long userId, Comment comment) {
        int likeCount = Math.toIntExact(commentLikeRepository.countByCommentId(comment.getId()));
        boolean isLiked = userId != null
            && commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId);

        return new CommentReplyResponse(
            comment.getId(),
            comment.getUser().getNickname(),
            comment.getContent(),
            comment.getCreatedAt(),
            likeCount,
            isLiked,
            Objects.equals(comment.getUser().getId(), userId)
        );
    }

    private Long getUserId(OAuth2User principal) {
        if (principal == null) {
            return null;
        }

        Object userIdAttribute = principal.getAttributes().get("userId");
        if (userIdAttribute == null) {
            return null;
        }

        if (userIdAttribute instanceof Number userIdNumber) {
            return userIdNumber.longValue();
        }

        if (userIdAttribute instanceof String userIdString) {
            try {
                return Long.parseLong(userIdString);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private String sanitizeCommentContent(String content) {
        String sanitized = Jsoup.clean(content, Safelist.none()).strip();
        if (sanitized.isBlank()) {
            throw new GeneralException(CommentExceptionType.COMMENT_CONTENT_INVALID);
        }
        return sanitized;
    }

}
