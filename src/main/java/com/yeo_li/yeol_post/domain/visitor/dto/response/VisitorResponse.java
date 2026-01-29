package com.yeo_li.yeol_post.domain.visitor.dto.response;


public record VisitorResponse(
    Long totalVisitorCount,
    Long todayVisitorCount
) {
    
}
