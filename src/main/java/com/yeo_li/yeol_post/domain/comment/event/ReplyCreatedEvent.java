package com.yeo_li.yeol_post.domain.comment.event;

import java.time.LocalDateTime;

public record ReplyCreatedEvent(
    Long replyId,
    Long replyAuthorUserId,
    String replyAuthorNickname,
    String replyContent,

    Long parentCommentId,
    Long parentCommentAuthorUserId,
    String parentCommentAuthorEmail,

    Long postId,
    String postTitle,

    LocalDateTime occurredAt
) {

}
