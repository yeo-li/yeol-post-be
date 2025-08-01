package com.yeo_li.yeol_post.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DraftPostCreateResponse {

  @JsonProperty("post_id")
  private Long postId;
}
