package com.yeo_li.yeol_post.streak.scheduling;

import com.yeo_li.yeol_post.streak.service.StreakService;
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
        log.info("주간 작업 시작");
        try {
            // 저번주(가장 최근 StreakStatus) StreakStatus 갱신
            streakService.updateStreakStatus(LocalDateTime.now());

            // 이번주 스트릭 생성
            streakService.createWeeklyStreak(LocalDateTime.now());
        } catch (Exception e) {
            log.error("주간 작업 실패", e);
        } finally {
            log.info("주간 작업 종료");
        }
    }
}
