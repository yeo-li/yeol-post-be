package com.yeo_li.yeol_post.domain.post.dto.command;

public record PostMailCommand(
    Long postId,
    String title,
    String summary
) {

}
