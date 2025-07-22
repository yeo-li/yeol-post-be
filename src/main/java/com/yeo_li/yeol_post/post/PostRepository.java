package com.yeo_li.yeol_post.post;

import com.yeo_li.yeol_post.category.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

  Post findPostById(Long id);

  @Query("SELECT p FROM Post p where LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isPublished = true")
  List<Post> searchPostByTitle(@Param("keyword") String keyword);

  List<Post> findPostsByCategory(Category category);

  @Query("SELECT p FROM Post p where LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isPublished = true")
  List<Post> findPostsByAuthor(@Param("keyword") String keyword);
}
