package com.yeo_li.yeol_post.category;

import com.yeo_li.yeol_post.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.common.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess(categoryService.getAllCategories()));
  }
}
