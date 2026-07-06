package com.yeo_li.yeol_post.domain.post.event;

import java.time.LocalDateTime;

public record CommentCreatedEvent(
    Long commentId,
    Long commentAuthorUserId,
    String commentAuthorNickname,
    String commentContent,

    Long postId,
    Long postAuthorUserId,
    String postAuthorEmail,
    String postTitle,

    LocalDateTime occurredAt
) {

}
