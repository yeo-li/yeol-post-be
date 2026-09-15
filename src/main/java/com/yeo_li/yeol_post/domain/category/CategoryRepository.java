package com.yeo_li.yeol_post.domain.category;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findCategoryByCategoryName(String categoryName);

    Category findCategoryById(Long categoryId);

    void deleteCategoryById(Long categoryId);

    @Query("""
        SELECT new com.yeo_li.yeol_post.domain.category.CategoryPostCount(
            p.category.id,
            COUNT(p.id)
        )
        FROM Post p
        WHERE p.category IN :categories
          AND p.isPublished = true
        GROUP BY p.category.id
        """)
    List<CategoryPostCount> countPublishedPostsByCategories(
        @Param("categories") List<Category> categories
    );
}
