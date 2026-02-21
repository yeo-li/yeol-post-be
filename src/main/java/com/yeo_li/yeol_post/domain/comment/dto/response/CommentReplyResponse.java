package com.yeo_li.yeol_post.domain.comment.dto.response;

import java.time.LocalDateTime;

public record CommentReplyResponse(
    Long commentId,
    String userNickname,
    String content,
    LocalDateTime createdAt,
    int likeCount,
    boolean isLiked,
    boolean isOwner
) {

}
