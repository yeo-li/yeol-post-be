package com.yeo_li.yeol_post.domain.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikeResponse(
    @Schema(description = "현재 로그인 사용자의 좋아요 여부", example = "true")
    boolean isLiked,
    @Schema(description = "해당 게시물의 좋아요 수", example = "12")
    int likeCount
) {

}
