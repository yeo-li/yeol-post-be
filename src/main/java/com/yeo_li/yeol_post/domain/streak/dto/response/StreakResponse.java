package com.yeo_li.yeol_post.domain.streak.dto.response;

import com.yeo_li.yeol_post.domain.streak.domain.WeeklyStreak;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StreakResponse {

    private final int currentStreakCount;

    private final List<WeeklyStreak> streaks;
}
