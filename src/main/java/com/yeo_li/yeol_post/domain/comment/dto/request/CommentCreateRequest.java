package com.yeo_li.yeol_post.domain.comment.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
    @NotNull String content
) {

}
