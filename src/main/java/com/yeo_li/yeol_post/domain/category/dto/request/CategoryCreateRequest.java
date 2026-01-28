package com.yeo_li.yeol_post.domain.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeo_li.yeol_post.domain.category.dto.command.CategoryCreateCommand;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
    @JsonProperty("category_name") @NotBlank
    String categoryName,
    @JsonProperty("category_color")
    String categoryColor,
    @JsonProperty("category_description")
    String categoryDescription

) {

    public CategoryCreateCommand toCommand() {
        return new CategoryCreateCommand(categoryName, categoryColor, categoryDescription);
    }
}
