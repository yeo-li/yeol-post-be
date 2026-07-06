package com.yeo_li.yeol_post.domain.subscription.dto.command;

import com.yeo_li.yeol_post.domain.comment.event.ReplyCreatedEvent;

public record ReplyMailCommand(
    String receiverEmail,
    Long replyId,
    Long postId,
    String postTitle,
    String replyAuthorNickname,
    String replyContent
) {

    public static ReplyMailCommand from(ReplyCreatedEvent event) {
        return new ReplyMailCommand(
            event.parentCommentAuthorEmail(),
            event.replyId(),
            event.postId(),
            event.postTitle(),
            event.replyAuthorNickname(),
            event.replyContent()
        );
    }
}
