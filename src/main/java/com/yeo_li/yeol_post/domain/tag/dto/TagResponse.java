package com.yeo_li.yeol_post.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 응답")
public record TagResponse(
    @Schema(description = "태그 ID", example = "1")
    @JsonProperty("tag_id")
    Long tagId,
    @Schema(description = "태그 이름", example = "spring")
    @JsonProperty("tag_name")
    String tagName
) {

}
