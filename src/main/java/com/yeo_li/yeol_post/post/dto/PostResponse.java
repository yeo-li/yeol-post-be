package com.yeo_li.yeol_post.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
    @NotNull
    Long postId,
    @NotBlank
    String title,
    String summary,
    @NotBlank
    String author,
    @NotBlank
    String content,
    @NotNull
    @JsonProperty("published_at")
    LocalDateTime publishedAt,
    String category,
    List<String> tags
) {

}
