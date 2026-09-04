package com.yeo_li.yeol_post.domain.subscription.dto.command;

import com.yeo_li.yeol_post.domain.comment.event.CommentLikedEvent;

public record CommentLikeMailCommand(
    String receiverEmail,
    Long commentId,
    Long postId,
    Long commentAuthorUserId,
    Long likerUserId,
    String postTitle,
    String likerNickname,
    String commentContent
) {

    public static CommentLikeMailCommand from(CommentLikedEvent event) {
        return new CommentLikeMailCommand(
            event.commentAuthorEmail(),
            event.commentId(),
            event.postId(),
            event.commentAuthorUserId(),
            event.likerUserId(),
            event.postTitle(),
            event.likerNickname(),
            event.commentContent()
        );
    }
}
