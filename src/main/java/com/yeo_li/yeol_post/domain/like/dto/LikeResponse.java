package com.yeo_li.yeol_post.domain.like.dto;

public record LikeResponse(
    boolean isLiked,
    int likeCount
) {

}
