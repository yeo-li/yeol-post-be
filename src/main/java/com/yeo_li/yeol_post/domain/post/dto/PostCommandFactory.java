package com.yeo_li.yeol_post.domain.post.dto;

import com.yeo_li.yeol_post.domain.admin.domain.Admin;
import com.yeo_li.yeol_post.domain.admin.repository.AdminRepository;
import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.category.CategoryRepository;
import com.yeo_li.yeol_post.domain.post.command.DraftPostCreateCommand;
import com.yeo_li.yeol_post.domain.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.tag.TagRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCommandFactory {

    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PostCreateCommand createPostCommand(PostCreateRequest request) {
        LocalDateTime updatedAt = LocalDateTime.now();
        Boolean isPublished = true;
        LocalDateTime publishedAt = LocalDateTime.now();

        Admin admin = adminRepository.findById(request.adminId())
            .orElseThrow(() -> new IllegalArgumentException(
                "PostCommandFactory:createPostCommand: Admin의 Id가 올바르지 않습니다."));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException(
                "PostCommandFactory:createPostCommand: Category의 Id가 올바르지 않습니다."));

        Boolean isDeleted = false;

        return new PostCreateCommand(
            request.title(),
            request.summary(),
            request.author(),
            request.content(),
            isPublished,
            publishedAt,
            admin,
            category,
            request.tags(),
            isDeleted
        );
    }

    public DraftPostCreateCommand createDraftPostCommand(PostCreateRequest request) {
        Boolean isPublished = false;

        Admin admin = adminRepository.findById(request.adminId())
            .orElseThrow(() -> new IllegalArgumentException(
                "PostCommandFactory:createPostCommand: Admin의 Id가 올바르지 않습니다."));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseGet(null);

        Boolean isDeleted = false;

        return new DraftPostCreateCommand(
            request.title(),
            request.summary(),
            request.author(),
            request.content(),
            isPublished,
            null,
            admin,
            category,
            request.tags(),
            isDeleted
        );
    }

    public PostMailCommand createPostMailCommand(Post post) {
        return new PostMailCommand(post.getId(), post.getTitle(), post.getSummary());
    }
}
