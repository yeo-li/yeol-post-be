package com.yeo_li.yeol_post.domain.post.event;

import java.time.LocalDateTime;

public record PostPublishedEvent(Long postId, String title, String summary,
                                 LocalDateTime publishedAt) {

}
