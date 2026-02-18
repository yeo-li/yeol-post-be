package com.yeo_li.yeol_post.domain.post.command;

import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record PostCreateCommand(
    @NotBlank
    String title, // 있음
    String summary, // 있음
    String author, // 있음
    String content, // 있음
    @NotNull
    Boolean isPublished, // Fact -
    LocalDateTime publishedAt, // Fact -
    @NotNull
    User user, // Fact -
    // todo : 카테고리 repository, save, findByName 구현
    Category category, // Fact -
    // todo : postTag repository, save, findByName 구현
    List<String> tags, // Fact -
    @NotNull
    Boolean isDeleted // Fact -
) {

    public Post toEntity() {
        Post post = new Post();

        post.setTitle(this.title);
        post.setSummary(this.summary);
        post.setAuthor(this.author);
        post.setContent(this.content);
        post.setPublishedAt(this.publishedAt);
        post.setIsPublished(this.isPublished);
        post.setIsDeleted(this.isDeleted);
        post.setCategory(this.category);
        post.setUser(this.user);

        return post;
    }
}
