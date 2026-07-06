package com.yeo_li.yeol_post.domain.comment.event;

import java.time.LocalDateTime;

public record CommentLikedEvent(
    Long commentId,
    String commentContent,
    Long commentAuthorUserId,
    String commentAuthorEmail,

    Long postId,
    String postTitle,

    Long likerUserId,
    String likerNickname,

    LocalDateTime occurredAt
) {

}
