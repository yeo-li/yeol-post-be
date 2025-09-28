package com.yeo_li.yeol_post.streak.service;

import com.yeo_li.yeol_post.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.post.domain.Post;
import com.yeo_li.yeol_post.streak.domain.StreakStatus;
import com.yeo_li.yeol_post.streak.domain.WeeklyStreak;
import com.yeo_li.yeol_post.streak.dto.response.StreakResponse;
import com.yeo_li.yeol_post.streak.exception.StreakExceptionType;
import com.yeo_li.yeol_post.streak.facade.StreakStatusRepositoryFacade;
import com.yeo_li.yeol_post.streak.facade.WeeklyStreakRepositoryFacade;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final WeeklyStreakRepositoryFacade weeklyStreakRepositoryFacade;
    private final StreakStatusRepositoryFacade streakStatusRepositoryFacade;

    public StreakResponse getStreaks() {
        List<WeeklyStreak> streaks = weeklyStreakRepositoryFacade.findRecentWeeklyStreaks();
        StreakStatus streakStatus = streakStatusRepositoryFacade.findLatest();
        return StreakResponse.builder()
            .currentStreakCount(streakStatus.getCurrentStreakLength())
            .streaks(streaks)
            .build();
    }

    @Transactional
    public void addStreakCount(LocalDateTime dateTime) {
        WeeklyStreak streak = weeklyStreakRepositoryFacade.findWeeklyStreakByDateTime(dateTime);
        if (streak == null) {
            throw new GeneralException(StreakExceptionType.WEEKLY_STREAK_NOT_FOUND);
        }

        streak.addPostCount();
    }

    @Transactional
    public void removeStreakCount(Post post) {
        if (!isThisWeek(post.getPublishedAt())) {
            return;
        }
        WeeklyStreak streak = weeklyStreakRepositoryFacade.findWeeklyStreakByDateTime(
            post.getPublishedAt());

        streak.removePostCount();
    }

    private boolean isThisWeek(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now().minusHours(6);

        LocalDateTime monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(LocalTime.MIN);

        LocalDateTime startOfWeek = monday.plusHours(6);

        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1).minusSeconds(1);

        return !dateTime.isBefore(startOfWeek) && !dateTime.isAfter(endOfWeek);
    }
}
