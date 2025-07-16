package com.yeo_li.yeol_post.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PostCreateRequest(
    @NotBlank
    String title,
    String summary,
    String author,
    @NotBlank
    String content,
    @NotNull
    @JsonProperty("admin_id") Long adminId,
    @JsonProperty("category_id")
    Long categoryId,
    List<String> tags) {

}
