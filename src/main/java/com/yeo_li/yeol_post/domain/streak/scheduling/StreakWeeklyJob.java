package com.yeo_li.yeol_post.domain.streak.scheduling;

import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreakWeeklyJob {

    private final StreakService streakService;

    //    @Scheduled(cron = "0 5 6 * * MON", zone = "Asia/Seoul")
    @Scheduled(cron = "0 5 6 * * MON", zone = "Asia/Seoul")
    public void runWeeklyTask() {
        try {
            // 저번주(가장 최근 StreakStatus) StreakStatus 갱신
            streakService.updateStreakStatus(LocalDateTime.now());

            // 이번주 스트릭 생성
            streakService.createWeeklyStreak(LocalDateTime.now());
        } catch (Exception e) {
            log.error(StructuredLog.event(
                    "STREAK_WEEKLY_JOB_FAILED",
                    "주간 스트릭 작업에 실패했습니다.",
                    "UNEXPECTED_ERROR"
                )
                .throwable(e)
                .build());
        }
    }
}
