package com.yeo_li.yeol_post.post;

import com.yeo_li.yeol_post.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.tag.Tag;
import com.yeo_li.yeol_post.tag.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;

  private final TagService tagService;

  public int createPost(PostCreateCommand command) {
    // 새로운 tag 저장
    List<Tag> tags = tagService.findOrCreateAll(command.tags());
    // post 저장
    Post post = postRepository.save(command.toEntity());

    // post_tag 저장

    return post.getId();
  }
}
