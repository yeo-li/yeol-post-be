package com.yeo_li.yeol_post.category;

import com.yeo_li.yeol_post.category.dto.command.CategoryCreateCommand;
import com.yeo_li.yeol_post.category.dto.request.CategoryUpdateRequest;
import com.yeo_li.yeol_post.category.dto.response.CategoryRecentResponse;
import com.yeo_li.yeol_post.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.category.exception.CategoryException;
import com.yeo_li.yeol_post.category.exception.CategoryExceptionType;
import com.yeo_li.yeol_post.post.domain.Post;
import com.yeo_li.yeol_post.post.dto.PostResponse;
import com.yeo_li.yeol_post.post.facade.PostRepositoryFacade;
import com.yeo_li.yeol_post.post_tag.PostTag;
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
            int postCnt = postRepositoryFacade.countPostByCategory(category);
//      if (postCnt == 0) {
//        continue;
//      }
            categoryResponses.add(CategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryDescription(category.getCategoryDescription())
                .categoryColor(category.getCategoryColor())
                .postCount(postCnt)
                .build());
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

        if (request.categoryName() != null) {
            category.setCategoryName(request.categoryName());
        }
        if (request.categoryColor() != null) {
            category.setCategoryColor(request.categoryColor());
        }
        if (request.categoryDescription() != null) {
            category.setCategoryDescription(request.categoryDescription());
        }

    }

    public List<CategoryRecentResponse> getAllCategoryRecentPost() {
        List<CategoryRecentResponse> categoryRecentResponses = new ArrayList<>();

        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            List<Post> posts = postRepositoryFacade.findPostsByCategory(category);
            if (posts.isEmpty()) {
                continue;
            }
            Post post = posts.getFirst();
            PostResponse postResponse = new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getSummary(),
                post.getAuthor(),
                post.getContent(),
                post.getViews(),
                post.getIsPublished(),
                post.getPublishedAt(),
                CategoryResponse.builder()
                    .categoryId(post.getCategory().getId())
                    .categoryName(post.getCategory().getCategoryName())
                    .categoryColor(post.getCategory().getCategoryColor())
                    .categoryDescription(post.getCategory().getCategoryColor())
                    .postCount(postRepositoryFacade.countPostByCategory(post.getCategory()))
                    .build(),
                toTagNames(post.getPostTags())
            );
            categoryRecentResponses.add(CategoryRecentResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getCategoryDescription())
                .categoryColor(category.getCategoryColor())
                .postCount(postResponse.category().postCount())
                .post(postResponse)
                .build());
        }
        return categoryRecentResponses;
    }

    private List<String> toTagNames(List<PostTag> postTags) {
        List<String> tagNames = new ArrayList<>();
        for (PostTag postTag : postTags) {
            tagNames.add(postTag.getTag().getTagName());
        }
        return tagNames;
    }

}
