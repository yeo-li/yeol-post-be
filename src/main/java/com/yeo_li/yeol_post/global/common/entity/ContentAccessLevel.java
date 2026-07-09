package com.yeo_li.yeol_post.global.common.entity;

import java.util.Arrays;
import java.util.List;

public enum ContentAccessLevel {
    PUBLIC(0),
    LIMITED(1),
    PRIVATE(2);

    private int priority;

    ContentAccessLevel(int priority) {
        this.priority = priority;
    }

    public List<ContentAccessLevel> accessibleLevels() {
        return Arrays.stream(values())
            .filter(level -> level.priority <= this.priority)
            .toList();
    }
}
