package com.yeo_li.yeol_post.domain.comment.dto.response;

import java.util.List;

public record CommentListResponse(
    List<CommentResponse> comments
) {

}
