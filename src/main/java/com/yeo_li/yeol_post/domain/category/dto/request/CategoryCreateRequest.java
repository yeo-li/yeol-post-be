package com.yeo_li.yeol_post.domain.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeo_li.yeol_post.domain.category.dto.command.CategoryCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카테고리 생성 요청")
public record CategoryCreateRequest(
    @Schema(description = "카테고리 이름", example = "Spring", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("category_name") @NotBlank
    String categoryName,
    @Schema(description = "카테고리 색상(Hex)", example = "#6DB33F")
    @JsonProperty("category_color")
    String categoryColor,
    @Schema(description = "카테고리 설명", example = "스프링 관련 글 모음")
    @JsonProperty("category_description")
    String categoryDescription

) {

    public CategoryCreateCommand toCommand() {
        return new CategoryCreateCommand(categoryName, categoryColor, categoryDescription);
    }
}
