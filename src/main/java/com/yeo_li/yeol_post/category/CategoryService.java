package com.yeo_li.yeol_post.category;

import com.yeo_li.yeol_post.category.dto.command.CategoryCreateCommand;
import com.yeo_li.yeol_post.category.dto.response.CategoryResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category findCategoryByCategoryName(String categoryName) {
    return categoryRepository.findCategoryByCategoryName(categoryName);
  }

  public Category findCategoryByCategoryId(Long categoryId) {
    return categoryRepository.findCategoryById(categoryId);
  }

  public List<CategoryResponse> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    List<CategoryResponse> categoryResponses = new ArrayList<>();
    for (Category category : categories) {
      categoryResponses.add(new CategoryResponse(category.getId(), category.getCategoryName()));
    }

    return categoryResponses;
  }

  public void saveCategory(CategoryCreateCommand command) {
    categoryRepository.save(command.toEntity());
  }
}
