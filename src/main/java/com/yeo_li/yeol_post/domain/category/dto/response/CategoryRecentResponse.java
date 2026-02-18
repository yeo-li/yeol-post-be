package com.yeo_li.yeol_post.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeo_li.yeol_post.domain.post.dto.PostResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "최근 게시물 포함 카테고리 응답")
public class CategoryRecentResponse {

    @Schema(description = "카테고리 ID", example = "1")
    @JsonProperty("category_id")
    private Long categoryId;

    @Schema(description = "카테고리 이름", example = "Spring")
    @JsonProperty("category_name")
    private String categoryName;

    @Schema(description = "카테고리 색상(Hex)", example = "#6DB33F")
    @JsonProperty("category_color")
    private String categoryColor;

    @Schema(description = "카테고리 설명", example = "스프링 관련 글 모음")
    @JsonProperty("category_description")
    private String description;

    @Schema(description = "카테고리 게시물 수", example = "12")
    @JsonProperty("post_count")
    private Integer postCount;

    @Schema(description = "카테고리의 최근 게시물")
    private PostResponse post;

}
