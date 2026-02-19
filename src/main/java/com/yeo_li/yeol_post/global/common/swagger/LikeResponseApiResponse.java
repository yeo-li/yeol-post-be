package com.yeo_li.yeol_post.global.common.swagger;

import com.yeo_li.yeol_post.domain.like.dto.LikeResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "result가 LikeResponse인 응답")
public class LikeResponseApiResponse {

    @Schema(description = "요청 성공 여부", example = "true")
    public Boolean isSuccess;

    @Schema(description = "상태 코드", example = "GLOBAL200")
    public String code;

    @Schema(description = "상태 메시지", example = "성공했습니다.")
    public String message;

    @Schema(description = "응답 결과", nullable = true)
    public LikeResponse result;
}
