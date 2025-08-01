package com.yeo_li.yeol_post.post.repository;

import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.post.domain.Post;
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

  @Query("select p from Post p where p.category = :category ORDER BY p.createdAt DESC")
  List<Post> findPostsByCategory(@Param("category") Category category);

//  @Query("SELECT p FROM Post p where LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isPublished = true")
//  List<Post> findPostsByAuthor(@Param("keyword") String keyword);

  @Query(
      value =
          "SELECT p.* " +
              "FROM post p WHERE p.is_published = :isPublished " +
              "ORDER BY p.published_at DESC " +
              "LIMIT :postCnt",
      nativeQuery = true
  )
  List<Post> findLatestPostsNative(@Param("postCnt") int postCnt,
      @Param("isPublished") boolean isPublished);


  @Query(
      value =
          "SELECT p.* " +
              "FROM post p " +
              "ORDER BY p.published_at DESC ",
      nativeQuery = true
  )
  List<Post> findAllPosts();

  List<Post> findPostsByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(Category category);

  List<Post> searchPostByTitleAndIsPublishedTrueOrderByPublishedAtDesc(String title);

  List<Post> findPostsByAuthorAndIsPublishedTrueOrderByIsPublishedDesc(String author);

  List<Post> findByIsPublishedTrueOrderByPublishedAtDesc();

  List<Post> findByIsPublishedFalseOrderByCreatedAtDesc();


}
