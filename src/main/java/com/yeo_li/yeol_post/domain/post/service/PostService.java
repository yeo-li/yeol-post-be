package com.yeo_li.yeol_post.domain.post.service;

import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.category.CategoryService;
import com.yeo_li.yeol_post.domain.category.dto.response.CategoryResponse;
import com.yeo_li.yeol_post.domain.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.dto.request.PostUpdateRequest;
import com.yeo_li.yeol_post.domain.post.dto.response.PostResponse;
import com.yeo_li.yeol_post.domain.post.event.PostPublishedEvent;
import com.yeo_li.yeol_post.domain.post.exception.PostExceptionType;
import com.yeo_li.yeol_post.domain.post.facade.PostRepositoryFacade;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.post_tag.PostTag;
import com.yeo_li.yeol_post.domain.post_tag.PostTagService;
import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import com.yeo_li.yeol_post.domain.tag.Tag;
import com.yeo_li.yeol_post.domain.tag.TagService;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.global.common.response.handler.PostHandler;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final TagService tagService;
    private final PostTagService postTagService;
    private final CategoryService categoryService;
    private final PostRepositoryFacade postRepositoryFacade;
    private final StreakService streakService;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public void createPost(PostCreateCommand command) {
        List<Tag> tags = tagService.findOrCreateAll(command.tags());

        Post post = postRepository.save(command.toEntity());

        postTagService.createPostTag(post, tags);

        publisher.publishEvent(
            new PostPublishedEvent(post.getId(), post.getTitle(), post.getSummary(),
                post.getPublishedAt())
        );

        log.info(StructuredLog.event(
                "POST_CREATED",
                "게시물이 생성되어 발행 상태로 전환되었습니다.",
                "CREATED"
            )
            .field("postId", post.getId())
            .field("userId", post.getUser().getId())
            .field("categoryId", post.getCategory().getId())
            .field("isPublished", post.getIsPublished())
            .field("tagCount", tags.size())
            .build());
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

        log.info(StructuredLog.event(
                "POST_DELETED",
                "게시물이 삭제되었습니다.",
                "DELETED"
            )
            .field("postId", post.getId())
            .field("userId", post.getUser().getId())
            .field("categoryId", post.getCategory().getId())
            .build());
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

        log.info(StructuredLog.event(
                "POST_UPDATED",
                "게시물 정보가 수정되었습니다.",
                "UPDATED"
            )
            .field("postId", post.getId())
            .field("userId", post.getUser().getId())
            .field("categoryId", post.getCategory().getId())
            .field("tagCount", tags.size())
            .build());
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

        log.info(StructuredLog.event(
                "POST_VIEW_COUNT_INCREASED",
                "게시물 조회수가 증가했습니다.",
                "INCREMENTED"
            )
            .field("postId", post.getId())
            .field("viewCount", post.getViews())
            .build());
    }

}
