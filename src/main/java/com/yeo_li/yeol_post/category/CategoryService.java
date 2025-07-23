package com.yeo_li.yeol_post.category;

import com.yeo_li.yeol_post.category.dto.command.CategoryCreateCommand;
import com.yeo_li.yeol_post.category.dto.request.CategoryUpdateRequest;
import com.yeo_li.yeol_post.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.category.exception.CategoryException;
import com.yeo_li.yeol_post.category.exception.CategoryExceptionType;
import com.yeo_li.yeol_post.post.facade.PostRepositoryFacade;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  private final PostRepositoryFacade postRepositoryFacade;

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

  @Transactional
  public void deleteCategory(Long categoryId) {
    Category category = categoryRepository.findCategoryById(categoryId);

    if (category == null) {
      throw new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND);
    }

    if (postRepositoryFacade.existsPostByCategoryId(category)) {
      throw new CategoryException(CategoryExceptionType.CATEGORY_NOT_DELETABLE);
    }

    categoryRepository.deleteCategoryById(categoryId);
  }

  @Transactional
  public void updateCategory(Long categoryId, CategoryUpdateRequest request) {
    Category category = categoryRepository.findCategoryById(categoryId);

    if (category == null) {
      throw new CategoryException(CategoryExceptionType.CATEGORY_NOT_FOUND);
    }

    category.setCategoryName(request.newCategoryName());
  }

}
