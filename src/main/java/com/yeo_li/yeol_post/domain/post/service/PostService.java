package com.yeo_li.yeol_post.domain.post.service;

import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.category.CategoryService;
import com.yeo_li.yeol_post.domain.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.global.common.response.handler.PostHandler;
import com.yeo_li.yeol_post.domain.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.dto.PostResponse;
import com.yeo_li.yeol_post.domain.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.domain.post.exception.PostExceptionType;
import com.yeo_li.yeol_post.domain.post.facade.PostRepositoryFacade;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.post_tag.PostTag;
import com.yeo_li.yeol_post.domain.post_tag.PostTagService;
import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import com.yeo_li.yeol_post.domain.tag.Tag;
import com.yeo_li.yeol_post.domain.tag.TagService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final PostRepositoryFacade postRepositoryFacade;
    private final StreakService streakService;

    public void createPost(PostCreateCommand command) {
        List<Tag> tags = tagService.findOrCreateAll(command.tags());
        Post post = postRepository.save(command.toEntity());
        postTagService.createPostTag(post, tags);
        streakService.addStreakCount(LocalDateTime.now());
    }

    public List<PostResponse> getAllPosts() {
        List<Post> allPosts = postRepositoryFacade.findAllPosts();
        return convertPostResponse(allPosts);
    }

    public List<PostResponse> getAllPublishedPosts() {
        List<Post> posts = postRepository.findByIsPublishedTrueOrderByPublishedAtDesc();
        return convertPostResponse(posts);
    }

    public List<PostResponse> getPostByTitle(String title) {
        if (title.isBlank()) {
            return null;
        }

        List<Post> posts = postRepository.searchPostByTitleAndIsPublishedTrueOrderByPublishedAtDesc(
            title);
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
        posts.sort((o1, o2) -> o1.getIsPublished().compareTo(o2.getIsPublished()));
        return convertPostResponse(posts);
    }

    public List<PostResponse> getPostByCategory(String categoryName) {
        if (categoryName.isBlank()) {
            return null;
        }

        Category category = categoryService.findCategoryByCategoryName(categoryName);
        List<Post> posts = postRepository.findPostsByCategoryAndIsPublishedTrueOrderByPublishedAtDesc(
            category);
        return convertPostResponse(posts);
    }

    public List<PostResponse> getPostByAuthor(String author) {
        if (author == null) {
            return null;
        }

        List<Post> posts = postRepository.findPostsByAuthorAndIsPublishedTrueOrderByIsPublishedDesc(
            author);
        return convertPostResponse(posts);
    }

    public List<PostResponse> getPostRecent(Integer postCnt, Boolean isPublished) {
        if (postCnt == null) {
            return null;
        }
        List<Post> posts = postRepositoryFacade.findLatestPostsNative(postCnt, isPublished);
        return convertPostResponse(posts);
    }

    public List<PostResponse> getPostRecent() {
        List<Post> posts = postRepositoryFacade.findLatestPostsNative(Integer.MAX_VALUE, true);
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
                post.getViews(),
                post.getIsPublished(),
                post.getPublishedAt(),
                CategoryResponse.builder()
                    .categoryId(post.getCategory().getId())
                    .categoryName(post.getCategory().getCategoryName())
                    .categoryColor(post.getCategory().getCategoryColor())
                    .categoryDescription(post.getCategory().getCategoryColor())
                    .postCount(postRepositoryFacade.countPostByCategory(post.getCategory()))
                    .build(),
                tagNames
            ));
        }

        return postResponses;
    }

    public void deletePostByPostId(Long postId) {
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            throw new GeneralException(PostExceptionType.POST_NOT_FOUND);
        }
        postRepository.deleteById(postId);
        streakService.removeStreakCount(post);
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request) {
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

    public List<PostResponse> getAllDraftPosts() {
        List<Post> posts = postRepository.findByIsPublishedFalseOrderByCreatedAtDesc();
        return convertPostResponse(posts);
    }

    @Transactional
    public void increaseViewCount(Long postId) {
        Post post = postRepository.findPostById(postId);
        if (post == null) {
            throw new PostHandler(ErrorStatus.VALIDATION_ERROR);
        }

        post.increaseViewCount();
    }

}
