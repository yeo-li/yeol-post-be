package com.yeo_li.yeol_post.domain.subscription.event;

public record AnnouncementRequestedEvent(
    String title,
    String content
) {

}
