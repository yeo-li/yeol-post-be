package com.yeo_li.yeol_post.domain.post.service;

import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.category.CategoryService;
import com.yeo_li.yeol_post.domain.post.command.DraftPostCreateCommand;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.dto.PostCommandFactory;
import com.yeo_li.yeol_post.domain.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.post_tag.PostTag;
import com.yeo_li.yeol_post.domain.post_tag.PostTagService;
import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import com.yeo_li.yeol_post.domain.subscription.service.NewsLetterService;
import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import com.yeo_li.yeol_post.domain.tag.Tag;
import com.yeo_li.yeol_post.domain.tag.TagService;
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
    private final StreakService streakService;
    private final NewsLetterService newsLetterService;
    private final SubscriptionService subscriptionService;
    private final PostCommandFactory postCommandFactory;

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
        streakService.addStreakCount(LocalDateTime.now());
        if (post.getIsPublished()) {
            newsLetterService.sendPublishedPostMails(
                subscriptionService.getSubscribedEmail(),
                postCommandFactory.createPostMailCommand(post));
        }
    }
}
