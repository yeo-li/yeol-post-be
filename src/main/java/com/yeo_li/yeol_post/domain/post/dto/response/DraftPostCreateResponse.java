package com.yeo_li.yeol_post.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DraftPostCreateResponse {

    @JsonProperty("post_id")
    private Long postId;
}
