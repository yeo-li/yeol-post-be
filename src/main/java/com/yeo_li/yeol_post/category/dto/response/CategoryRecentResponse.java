package com.yeo_li.yeol_post.category.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeo_li.yeol_post.post.dto.PostResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryRecentResponse {

  @JsonProperty("category_id")
  private Long categoryId;

  @JsonProperty("category_name")
  private String categoryName;

  @JsonProperty("category_color")
  private String categoryColor;

  @JsonProperty("category_description")
  private String description;

  @JsonProperty("post_count")
  private Integer postCount;
  
  private PostResponse post;

}
