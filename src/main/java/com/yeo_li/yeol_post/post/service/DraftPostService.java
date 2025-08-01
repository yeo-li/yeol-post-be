package com.yeo_li.yeol_post.post.service;

import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.category.CategoryService;
import com.yeo_li.yeol_post.post.command.DraftPostCreateCommand;
import com.yeo_li.yeol_post.post.domain.Post;
import com.yeo_li.yeol_post.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.post.repository.PostRepository;
import com.yeo_li.yeol_post.post_tag.PostTag;
import com.yeo_li.yeol_post.post_tag.PostTagService;
import com.yeo_li.yeol_post.tag.Tag;
import com.yeo_li.yeol_post.tag.TagService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DraftPostService {

  private final TagService tagService;
  private final PostRepository postRepository;
  private final PostTagService postTagService;
  private final CategoryService categoryService;

  public Long createDraftPost(DraftPostCreateCommand command) {
    List<Tag> tags = tagService.findOrCreateAll(command.tags());

    Post post = postRepository.save(command.toEntity());
    postTagService.createPostTag(post, tags);

    return post.getId();
  }

  @Transactional
  public void updateDraftPost(Long postId, PostUpdateRequest request) {
    Post post = postRepository.findPostById(postId);

    if (request.title() != null) {
      post.setTitle(request.title());
    }
    if (request.summary() != null) {
      post.setSummary(request.summary());
    }
    if (request.content() != null) {
      post.setContent(request.content());
    }
    if (request.author() != null) {
      post.setAuthor(request.author());
    }
    if (request.categoryId() != null) {
      Category category = categoryService.findCategoryByCategoryId(request.categoryId());
      post.setCategory(category);
    }

    List<Tag> tags = tagService.findOrCreateAll(request.tags());

    List<PostTag> postTags = postTagService.findPostTagByPostId(postId);
    for (PostTag postTag : postTags) {
      postTagService.deletePostTag(postTag.getId());
    }

    postTagService.createPostTag(post, tags);
  }

  @Transactional
  public void publishPost(Long postId) {
    Post post = postRepository.findPostById(postId);
    post.setIsPublished(true);
    post.setPublishedAt(LocalDateTime.now());
  }
}
