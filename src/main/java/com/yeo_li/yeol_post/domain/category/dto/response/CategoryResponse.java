package com.yeo_li.yeol_post.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "카테고리 응답")
public record CategoryResponse(
    @Schema(description = "카테고리 ID", example = "1")
    @JsonProperty("category_id")
    Long categoryId,
    @Schema(description = "카테고리 이름", example = "Spring")
    @JsonProperty("category_name")
    String categoryName,
    @Schema(description = "카테고리 설명", example = "스프링 관련 글 모음")
    @JsonProperty("category_description")
    String categoryDescription,
    @Schema(description = "카테고리 색상(Hex)", example = "#6DB33F")
    @JsonProperty("category_color")
    String categoryColor,
    @Schema(description = "카테고리 게시물 수", example = "12")
    @JsonProperty("post_count")
    Integer postCount
) {

}
