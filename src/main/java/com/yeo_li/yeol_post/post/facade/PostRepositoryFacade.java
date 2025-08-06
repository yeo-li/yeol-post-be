package com.yeo_li.yeol_post.post.facade;

import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.common.response.handler.PostHandler;
import com.yeo_li.yeol_post.post.domain.Post;
import com.yeo_li.yeol_post.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostRepositoryFacade {

  private final PostRepository postRepository;

  public boolean existsPostByCategoryId(Category category) {
    List<Post> posts = postRepository.findPostsByCategory(category);
    return !posts.isEmpty();
  }

  public List<Post> findLatestPostsNative(Integer postCnt, boolean isPublished) {
    if (postCnt == null) {
      throw new PostHandler(ErrorStatus.VALIDATION_ERROR);
    }

    return postRepository.findLatestPostsNative(postCnt, isPublished);
  }

  public int countPostByCategory(Category category) {
    List<Post> posts = postRepository.findPostsByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(
        category);

    return posts.size();
  }

  public List<Post> findPostsByCategory(Category category) {
    return postRepository.findPostsByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(category);
  }

  public List<Post> findAllPosts() {
    return postRepository.findAllPosts();
  }

}
