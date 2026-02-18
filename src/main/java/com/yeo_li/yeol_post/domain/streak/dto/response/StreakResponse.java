package com.yeo_li.yeol_post.domain.streak.dto.response;

import com.yeo_li.yeol_post.domain.streak.domain.WeeklyStreak;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "주간 스트릭 응답")
public class StreakResponse {

    @Schema(description = "현재 연속 작성 주차 수", example = "5")
    private final int currentStreakCount;

    @Schema(description = "주차별 작성 기록")
    private final List<WeeklyStreak> streaks;
}
