package com.yeo_li.yeol_post.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TagResponse(
    @JsonProperty("tag_id")
    Long tagId,
    @JsonProperty("tag_name")
    String tagName
) {

}
