package com.yeo_li.yeol_post.domain.like.event;

import java.time.LocalDateTime;

public record PostLikedEvent(
    Long postId,
    String postTitle,
    Long postAuthorUserId,
    String postAuthorEmail,

    Long likerUserId,
    String likerNickname,

    LocalDateTime occurredAt
) {

}
