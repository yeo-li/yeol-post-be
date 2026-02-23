package com.yeo_li.yeol_post.domain.comment.service;

import com.yeo_li.yeol_post.domain.comment.domain.Comment;
import com.yeo_li.yeol_post.domain.comment.dto.request.CommentCreateRequest;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentListResponse;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentReplyResponse;
import com.yeo_li.yeol_post.domain.comment.dto.response.CommentResponse;
import com.yeo_li.yeol_post.domain.comment.exception.CommentExceptionType;
import com.yeo_li.yeol_post.domain.comment.repository.CommentRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse saveComment(OAuth2User principal, Long postId, CommentCreateRequest request) {
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

        Comment savedComment = commentRepository.save(
            new Comment(request.content(), post, user, null)
        );

        return convertCommentResponse(userId, savedComment);
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

            List<Comment> replies = commentRepository.findCommentsByParentComment(comment);
            for (Comment reply : replies) {
                CommentReplyResponse commentReplyResponse = convertCommentReplyResponse(userId,
                    reply);
                commentResponse.replies().add(commentReplyResponse);
            }

            commentResponses.add(commentResponse);
        }

        return new CommentListResponse(commentResponses);
    }

    private CommentResponse convertCommentResponse(Long userId, Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getUser().getNickname(),
            comment.getContent(),
            comment.getCreatedAt(),
            0, // TODO: 아직 구현 안됨. 일단 댓글 api 다 만들고 좋아요로 넘어갈거임
            false, // TODO: 아직 구현 안됨. 일단 댓글 api 다 만들고 좋아요로 넘어갈거임
            Objects.equals(comment.getUser().getId(), userId),
            new ArrayList<>()
        );
    }

    private CommentReplyResponse convertCommentReplyResponse(Long userId, Comment comment) {
        return new CommentReplyResponse(
            comment.getId(),
            comment.getUser().getNickname(),
            comment.getContent(),
            comment.getCreatedAt(),
            0, // TODO: 아직 구현 안됨. 일단 댓글 api 다 만들고 좋아요로 넘어갈거임
            false, // TODO: 아직 구현 안됨. 일단 댓글 api 다 만들고 좋아요로 넘어갈거임
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

}
