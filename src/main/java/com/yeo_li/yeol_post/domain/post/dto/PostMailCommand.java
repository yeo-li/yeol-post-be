package com.yeo_li.yeol_post.domain.post.dto;

public record PostMailCommand(
    Long postId,
    String title,
    String summary
) {

}
