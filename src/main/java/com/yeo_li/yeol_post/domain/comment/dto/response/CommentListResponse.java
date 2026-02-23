package com.yeo_li.yeol_post.domain.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시물 댓글 목록 응답")
public record CommentListResponse(
    @Schema(description = "댓글 목록")
    List<CommentResponse> comments
) {

}
