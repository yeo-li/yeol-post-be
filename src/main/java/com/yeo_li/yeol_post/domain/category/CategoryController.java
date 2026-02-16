package com.yeo_li.yeol_post.domain.category;

import com.yeo_li.yeol_post.domain.auth.AuthorizationService;
import com.yeo_li.yeol_post.domain.category.dto.request.CategoryCreateRequest;
import com.yeo_li.yeol_post.domain.category.dto.request.CategoryUpdateRequest;
import com.yeo_li.yeol_post.domain.category.dto.response.CategoryRecentResponse;
import com.yeo_li.yeol_post.domain.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final AuthorizationService authorizationService;

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(categoryService.getAllCategories()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createCategory(
        @RequestBody @Valid CategoryCreateRequest request) {

        categoryService.saveCategory(request.toCommand());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(
        @PathVariable Long categoryId
    ) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
        @PathVariable Long categoryId,
        @RequestBody @Valid CategoryUpdateRequest request
    ) {

        categoryService.updateCategory(categoryId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @GetMapping("recent")
    public ResponseEntity<ApiResponse<List<CategoryRecentResponse>>> getRecentPublishedCategories() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(categoryService.getAllCategoryRecentPost()));
    }
}
