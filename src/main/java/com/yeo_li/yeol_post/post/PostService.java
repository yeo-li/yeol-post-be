package com.yeo_li.yeol_post.post;

import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.category.CategoryService;
import com.yeo_li.yeol_post.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.post.dto.PostResponse;
import com.yeo_li.yeol_post.post_tag.PostTagService;
import com.yeo_li.yeol_post.tag.Tag;
import com.yeo_li.yeol_post.tag.TagService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;

  private final TagService tagService;
  private final PostTagService postTagService;
  private final CategoryService categoryService;

  public int createPost(PostCreateCommand command) {
    List<Tag> tags = tagService.findOrCreateAll(command.tags());
    Post post = postRepository.save(command.toEntity());
    postTagService.createPostTag(post, tags);

    return post.getId();
  }

  public List<PostResponse> getPostByTitle(String title) {
    if (title.isBlank()) {
      return null;
    }

    List<Post> posts = postRepository.searchPostByTitle(title);
    if (posts == null) {
      return null;
    }

    posts.sort((p1, p2) -> p2.getPublishedAt().compareTo(p1.getPublishedAt()));
    return convertPostResponse(posts);
  }

  public List<PostResponse> getPostByTag(String tagName) {
    if (tagName.isBlank()) {
      return null;
    }
    Tag tag = tagService.findTagByTagName(tagName);
    List<Post> posts = postTagService.findPostByTagId(tag.getId());
    return convertPostResponse(posts);
  }

  public List<PostResponse> getPostByCategory(String categoryName) {
    if (categoryName.isBlank()) {
      return null;
    }

    Category category = categoryService.findCategoryByCategoryName(categoryName);
    List<Post> posts = postRepository.findPostsByCategory(category);
    return convertPostResponse(posts);
  }

  public List<PostResponse> getPostByAuthor(String author) {
    if (author == null) {
      return null;
    }

    List<Post> posts = postRepository.findPostsByAuthor(author);
    return convertPostResponse(posts);
  }

  public List<PostResponse> convertPostResponse(List<Post> posts) {
    List<PostResponse> postResponses = new ArrayList<>();
    for (Post post : posts) {
      List<Tag> tags = postTagService.findTagByPostId(post.getId());
      List<String> tagNames = new ArrayList<>();
      for (Tag tag : tags) {
        tagNames.add(tag.getTagName());
      }

      postResponses.add(new PostResponse(
          post.getId(),
          post.getTitle(),
          post.getSummary(),
          post.getAuthor(),
          post.getContent(),
          post.getPublishedAt(),
          post.getCategory().getCategoryName(),
          tagNames
      ));
    }

    return postResponses;
  }

}
