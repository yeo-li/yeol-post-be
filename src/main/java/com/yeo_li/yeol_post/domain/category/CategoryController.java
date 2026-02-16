package com.yeo_li.yeol_post.domain.category;

import com.yeo_li.yeol_post.domain.category.dto.request.CategoryCreateRequest;
import com.yeo_li.yeol_post.domain.category.dto.request.CategoryUpdateRequest;
import com.yeo_li.yeol_post.domain.category.dto.response.CategoryRecentResponse;
import com.yeo_li.yeol_post.domain.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 목록 조회", description = "등록된 전체 카테고리를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": [
                        {
                          "category_id": 1,
                          "category_name": "Spring",
                          "category_description": "스프링 관련 글 모음",
                          "category_color": "#6DB33F",
                          "post_count": 12
                        }
                      ]
                    }
                    """)
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(categoryService.getAllCategories()));
    }

    @Operation(summary = "카테고리 생성", description = "새 카테고리를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "생성 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "카테고리 생성 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "category_name": "Backend",
                  "category_color": "#0F4C81",
                  "category_description": "백엔드 개발 글"
                }
                """)
        )
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createCategory(
        @RequestBody @Valid CategoryCreateRequest request) {

        categoryService.saveCategory(request.toCommand());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리 ID로 카테고리를 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "삭제 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(
        @Parameter(description = "삭제할 카테고리 ID", example = "1")
        @PathVariable Long categoryId
    ) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 이름/색상/설명을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "카테고리 수정 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "category_name": "Infra",
                  "category_color": "#1F2937",
                  "category_description": "인프라 관련 글"
                }
                """)
        )
    )
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
        @Parameter(description = "수정할 카테고리 ID", example = "1")
        @PathVariable Long categoryId,
        @RequestBody @Valid CategoryUpdateRequest request
    ) {

        categoryService.updateCategory(categoryId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "최근 카테고리 조회", description = "카테고리별 최근 게시물 정보를 함께 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": [
                        {
                          "category_id": 1,
                          "category_name": "Spring",
                          "category_color": "#6DB33F",
                          "category_description": "스프링 관련 글 모음",
                          "post_count": 12,
                          "post": {
                            "postId": 99,
                            "title": "Spring Security 시작",
                            "summary": "인증/인가 기초",
                            "author": "yeoli",
                            "content": "본문",
                            "views": 120,
                            "is_published": true,
                            "published_at": "2026-02-16T09:30:00",
                            "category": null,
                            "tags": [
                              "spring",
                              "security"
                            ]
                          }
                        }
                      ]
                    }
                    """)
            )
        )
    })
    @GetMapping("recent")
    public ResponseEntity<ApiResponse<List<CategoryRecentResponse>>> getRecentPublishedCategories() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(categoryService.getAllCategoryRecentPost()));
    }
}
