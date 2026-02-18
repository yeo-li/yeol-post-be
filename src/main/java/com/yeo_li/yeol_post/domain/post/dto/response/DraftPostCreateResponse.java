package com.yeo_li.yeol_post.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "임시 게시물 생성 응답")
public class DraftPostCreateResponse {

    @Schema(description = "생성된 임시 게시물 ID", example = "101")
    @JsonProperty("post_id")
    private Long postId;
}
