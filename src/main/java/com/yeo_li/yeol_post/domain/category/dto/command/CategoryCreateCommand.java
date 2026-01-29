package com.yeo_li.yeol_post.domain.category.dto.command;

import com.yeo_li.yeol_post.domain.category.Category;

public record CategoryCreateCommand(
    String categoryName,
    String categoryColor,
    String categoryDescription
) {

    public Category toEntity() {
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCategoryColor(categoryColor);
        category.setCategoryDescription(categoryDescription);

        return category;
    }
}
