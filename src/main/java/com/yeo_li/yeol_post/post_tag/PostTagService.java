package com.yeo_li.yeol_post.post_tag;

import com.yeo_li.yeol_post.post.Post;
import com.yeo_li.yeol_post.tag.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostTagService {

  private final PostTagRepository postTagRepository;


  public void createPostTag(Post post, List<Tag> tags) {
    for (Tag tag : tags) {
      postTagRepository.save(new PostTag(post, tag));
    }
  }
}
