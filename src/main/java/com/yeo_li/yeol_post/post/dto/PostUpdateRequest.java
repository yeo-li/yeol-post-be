package com.yeo_li.yeol_post.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PostUpdateRequest(
    @JsonProperty("post_id")
    Long postId,
    String title,
    String content,
    String summary,
    String author,
    @JsonProperty("category_id")
    Long categoryId,
    @JsonProperty("tags")
    List<String> tags
) {

}
