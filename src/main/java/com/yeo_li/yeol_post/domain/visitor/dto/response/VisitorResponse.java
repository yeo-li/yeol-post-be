package com.yeo_li.yeol_post.domain.visitor.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방문자 통계 응답")
public record VisitorResponse(
    @Schema(description = "누적 방문자 수", example = "20240")
    Long totalVisitorCount,
    @Schema(description = "오늘 방문자 수", example = "312")
    Long todayVisitorCount
) {
    
}
