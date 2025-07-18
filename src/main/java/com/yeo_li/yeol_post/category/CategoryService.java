package com.yeo_li.yeol_post.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category findCategoryByCategoryName(String categoryName) {
    return categoryRepository.findCategoryByCategoryName(categoryName);
  }
}
