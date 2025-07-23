package com.yeo_li.yeol_post.post.facade;

import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.post.Post;
import com.yeo_li.yeol_post.post.PostRepository;
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
}
