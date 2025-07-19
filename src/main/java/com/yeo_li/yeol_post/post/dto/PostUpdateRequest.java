package com.yeo_li.yeol_post.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PostUpdateRequest(
    @JsonProperty("post_id")
    Integer postId,
    String title,
    String content,
    String summary,
    String author,
    @JsonProperty("category_id")
    Integer categoryId,
    @JsonProperty("tag_ids")
    List<Integer> tagIds
) {

}
