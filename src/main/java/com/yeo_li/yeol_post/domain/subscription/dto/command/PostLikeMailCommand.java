package com.yeo_li.yeol_post.domain.subscription.dto.command;

import com.yeo_li.yeol_post.domain.like.event.PostLikedEvent;

public record PostLikeMailCommand(
    String receiverEmail,
    Long postId,
    Long postAuthorUserId,
    Long likerUserId,
    String postTitle,
    String likerNickname
) {

    public static PostLikeMailCommand from(PostLikedEvent event) {
        return new PostLikeMailCommand(
            event.postAuthorEmail(),
            event.postId(),
            event.postAuthorUserId(),
            event.likerUserId(),
            event.postTitle(),
            event.likerNickname()
        );
    }
}
