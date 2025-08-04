package com.yeo_li.yeol_post.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
    @JsonProperty("category_name")
    @Size(max = 20, message = "카테고리는 20자 이하로 입력해주세요.")
    String categoryName,
    @JsonProperty("category_color")
    String categoryColor,
    @JsonProperty("category_description")
    String categoryDescription
) {

}
