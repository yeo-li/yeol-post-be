package com.yeo_li.yeol_post.category.dto.command;

import com.yeo_li.yeol_post.category.Category;

public record CategoryCreateCommand(
    String categoryName
) {

  public Category toEntity() {
    Category category = new Category();
    category.setCategoryName(categoryName);

    return category;
  }
}
