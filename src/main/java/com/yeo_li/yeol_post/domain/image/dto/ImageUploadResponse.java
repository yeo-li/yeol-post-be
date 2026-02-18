package com.yeo_li.yeol_post.domain.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이미지 업로드 응답")
public class ImageUploadResponse {

    @Schema(description = "업로드된 이미지 URL", example = "https://api.example.com/api/v1/images/3f5b2a.png")
    private final String url;
}
