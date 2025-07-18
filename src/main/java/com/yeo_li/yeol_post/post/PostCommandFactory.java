package com.yeo_li.yeol_post.post;

import com.yeo_li.yeol_post.admin.Admin;
import com.yeo_li.yeol_post.admin.AdminRepository;
import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.category.CategoryRepository;
import com.yeo_li.yeol_post.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.post.dto.PostCreateRequest;
import com.yeo_li.yeol_post.tag.TagRepository;
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
    Boolean isPublished = false;
    LocalDateTime publishedAt = null;

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
        updatedAt,
        isPublished,
        null,
        admin,
        category,
        request.tags(),
        isDeleted
    );
  }
}
