package com.yeo_li.yeol_post.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryResponse(
    @JsonProperty("category_id")
    Long categoryId,
    @JsonProperty("category_name")
    String categoryName
) {

}
