package com.yeo_li.yeol_post.domain.comment.service;

import com.yeo_li.yeol_post.domain.comment.domain.Comment;
import com.yeo_li.yeol_post.domain.comment.domain.CommentLike;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentUpdateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentListResponse;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentReplyResponse;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentResponse;
import com.yeo_li.yeol_post.domain.comment.event.CommentLikedEvent;
import com.yeo_li.yeol_post.domain.comment.event.ReplyCreatedEvent;
import com.yeo_li.yeol_post.domain.comment.exception.CommentExceptionType;
import com.yeo_li.yeol_post.domain.comment.repository.CommentLikeRepository;
import com.yeo_li.yeol_post.domain.comment.repository.CommentRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.event.CommentCreatedEvent;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher publisher;

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

        publisher.publishEvent(new CommentCreatedEvent(savedComment.getId(), savedComment.getUser().getId(),
            savedComment.getUser().getNickname(), savedComment.getContent(), post.getId(),
            post.getUser().getId(), post.getUser().getEmail(), post.getTitle(), LocalDateTime.now()));

        log.info(StructuredLog.event(
                "COMMENT_CREATED",
                "댓글이 생성되었습니다.",
                "CREATED"
            )
            .field("commentId", savedComment.getId())
            .field("postId", post.getId())
            .field("userId", user.getId())
            .field("postOwnerUserId", post.getUser().getId())
            .build());

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

        log.info(StructuredLog.event(
                "COMMENT_DELETED",
                "댓글이 삭제 상태로 전환되었습니다.",
                "DELETED"
            )
            .field("commentId", comment.getId())
            .field("postId", comment.getPost() == null ? null : comment.getPost().getId())
            .field("userId", userId)
            .build());
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

        log.info(StructuredLog.event(
                "COMMENT_UPDATED",
                "댓글 내용이 수정되었습니다.",
                "UPDATED"
            )
            .field("commentId", comment.getId())
            .field("postId", comment.getPost() == null ? null : comment.getPost().getId())
            .field("userId", userId)
            .build());
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

        publisher.publishEvent(
            new ReplyCreatedEvent(reply.getId(), reply.getUser().getId(), reply.getUser().getNickname(),
                reply.getContent(), parentComment.getId(), parentComment.getUser().getId(),
                parentComment.getUser().getEmail(), parentComment.getPost().getId(), parentComment.getPost().getTitle(),
                LocalDateTime.now()));

        log.info(StructuredLog.event(
                "COMMENT_REPLY_CREATED",
                "댓글 답글이 생성되었습니다.",
                "CREATED"
            )
            .field("replyId", reply.getId())
            .field("parentCommentId", parentComment.getId())
            .field("postId", parentComment.getPost().getId())
            .field("userId", user.getId())
            .field("parentCommentOwnerUserId", parentComment.getUser().getId())
            .build());

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
        publisher.publishEvent(new CommentLikedEvent(
            comment.getId(),
            comment.getContent(),
            comment.getUser().getId(),
            comment.getUser().getEmail(),
            comment.getPost().getId(),
            comment.getPost().getTitle(),
            user.getId(),
            user.getNickname(),
            LocalDateTime.now()
        ));

        log.info(StructuredLog.event(
                "COMMENT_LIKED",
                "댓글 좋아요가 반영되었습니다.",
                "APPLIED"
            )
            .field("commentId", comment.getId())
            .field("postId", comment.getPost().getId())
            .field("userId", user.getId())
            .field("commentOwnerUserId", comment.getUser().getId())
            .build());
    }

    @Transactional
    public void unlikeComment(OAuth2User principal, Long commentId) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(CommentExceptionType.COMMENT_USER_ID_INVALID);
        }

        userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_USER_NOT_FOUND));

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new GeneralException(CommentExceptionType.COMMENT_NOT_FOUND));

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);

        log.info(StructuredLog.event(
                "COMMENT_UNLIKED",
                "댓글 좋아요 취소가 반영되었습니다.",
                "APPLIED"
            )
            .field("commentId", commentId)
            .field("postId", comment.getPost() == null ? null : comment.getPost().getId())
            .field("userId", userId)
            .field("commentOwnerUserId", comment.getUser() == null ? null : comment.getUser().getId())
            .build());
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
