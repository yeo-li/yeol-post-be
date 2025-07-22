package com.yeo_li.yeol_post.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeo_li.yeol_post.category.dto.command.CategoryCreateCommand;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
    @JsonProperty("category_name") @NotBlank
    String categoryName
) {

  public CategoryCreateCommand toCommand() {
    return new CategoryCreateCommand(categoryName);
  }
}
