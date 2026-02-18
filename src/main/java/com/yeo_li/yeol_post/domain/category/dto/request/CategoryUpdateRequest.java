package com.yeo_li.yeol_post.domain.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "카테고리 수정 요청")
public record CategoryUpdateRequest(
    @Schema(description = "카테고리 이름", example = "Backend")
    @JsonProperty("category_name")
    @Size(max = 20, message = "카테고리는 20자 이하로 입력해주세요.")
    String categoryName,
    @Schema(description = "카테고리 색상(Hex)", example = "#0F4C81")
    @JsonProperty("category_color")
    String categoryColor,
    @Schema(description = "카테고리 설명", example = "백엔드 개발 글")
    @JsonProperty("category_description")
    String categoryDescription
) {

}
