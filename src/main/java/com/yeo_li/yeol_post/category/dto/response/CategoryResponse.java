package com.yeo_li.yeol_post.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CategoryResponse(
    @JsonProperty("category_id")
    Long categoryId,
    @JsonProperty("category_name")
    String categoryName,
    @JsonProperty("category_description")
    String categoryDescription,
    @JsonProperty("category_color")
    String categoryColor,
    @JsonProperty("post_count")
    Integer postCount
) {

}
