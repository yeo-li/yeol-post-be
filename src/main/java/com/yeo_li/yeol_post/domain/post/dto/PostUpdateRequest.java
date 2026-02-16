package com.yeo_li.yeol_post.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시물 수정 요청")
public record PostUpdateRequest(
    @Schema(description = "게시물 ID", example = "10")
    @JsonProperty("post_id")
    Long postId,
    @Schema(description = "수정할 제목", example = "Spring Security 인증/인가 정리")
    String title,
    @Schema(description = "수정할 본문", example = "인증 인가 흐름을 단계별로 설명합니다.")
    String content,
    @Schema(description = "수정할 요약", example = "Spring Security 핵심 요약")
    String summary,
    @Schema(description = "작성자", example = "yeoli")
    String author,
    @Schema(description = "카테고리 ID", example = "2")
    @JsonProperty("category_id")
    Long categoryId,
    @Schema(description = "태그 목록", example = "[\"spring\", \"security\"]")
    @JsonProperty("tags")
    List<String> tags
) {

}
