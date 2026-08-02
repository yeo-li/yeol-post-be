package com.yeo_li.yeol_post.domain.feed.dto.response;

import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import java.time.LocalDateTime;

public record FeedResponse(
    Long feedId,
    String authorNickname,
    String content,
    ContentAccessLevel requiredAccessLevel,
    LocalDateTime createdAt,
    boolean isOwner,
    long likeCount,
    boolean isLiked
) {

}
