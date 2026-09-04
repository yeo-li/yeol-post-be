package com.yeo_li.yeol_post.domain.subscription.dto.command;

import com.yeo_li.yeol_post.domain.post.event.CommentCreatedEvent;

public record CommentMailCommand(
    String receiverEmail,
    Long commentId,
    Long postId,
    Long commentAuthorUserId,
    Long postAuthorUserId,
    String postTitle,
    String commentAuthorNickname,
    String commentContent
) {

    public static CommentMailCommand from(CommentCreatedEvent event) {
        return new CommentMailCommand(
            event.postAuthorEmail(),
            event.commentId(),
            event.postId(),
            event.commentAuthorUserId(),
            event.postAuthorUserId(),
            event.postTitle(),
            event.commentAuthorNickname(),
            event.commentContent()
        );
    }
}
