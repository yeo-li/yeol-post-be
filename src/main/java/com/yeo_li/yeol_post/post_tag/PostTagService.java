package com.yeo_li.yeol_post.post_tag;

import com.yeo_li.yeol_post.post.Post;
import com.yeo_li.yeol_post.tag.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostTagService {

  private final PostTagRepository postTagRepository;


  public void createPostTag(Post post, List<Tag> tags) {
    for (Tag tag : tags) {
      postTagRepository.save(new PostTag(post, tag));
    }
  }

  public List<Tag> findTagByPostId(Long postId) {
    List<PostTag> postTags = postTagRepository.findPostTagsByPost_Id(postId);
    List<Tag> tags = new ArrayList<>();
    for (PostTag postTag : postTags) {
      tags.add(postTag.getTag());
    }

    return tags;
  }

  public List<Post> findPostByTagId(Long tagId) {
    List<PostTag> postTags = postTagRepository.findPostTagsByTag_Id(tagId);
    List<Post> posts = new ArrayList<>();
    for (PostTag postTag : postTags) {
      posts.add(postTag.getPost());
    }

    return posts;
  }

  public List<PostTag> findPostTagByPostId(Long postId) {
    return postTagRepository.findPostTagsByPost_Id(postId);
  }

  @Transactional
  public void deletePostTag(Long postTagId) {
    postTagRepository.deleteById(postTagId);
  }
}
