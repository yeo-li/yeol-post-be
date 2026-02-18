package com.yeo_li.yeol_post.domain.post.command;

import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DraftPostCreateCommand(
    @NotBlank
    String title,
    String summary,
    String author,
    @NotBlank
    String content,
    @NotNull
    Boolean isPublished,
    LocalDateTime publishedAt,
    User user,
    Category category,
    List<String> tags,
    @NotNull
    Boolean isDeleted
) {

    public Post toEntity() {
        Post post = new Post();

        post.setTitle(this.title);
        post.setSummary(this.summary);
        post.setAuthor(this.author);
        post.setContent(this.content);
        post.setPublishedAt(null);
        post.setIsPublished(false);
        post.setIsDeleted(this.isDeleted);
        post.setCategory(this.category);
        post.setUser(this.user);

        return post;
    }
}

