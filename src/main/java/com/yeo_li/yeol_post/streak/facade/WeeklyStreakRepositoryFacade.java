package com.yeo_li.yeol_post.streak.facade;

import com.yeo_li.yeol_post.streak.domain.WeeklyStreak;
import com.yeo_li.yeol_post.streak.repository.WeeklyStreakRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyStreakRepositoryFacade {

    private final WeeklyStreakRepository weeklyStreakRepository;

    public List<WeeklyStreak> findRecentWeeklyStreaks() {
        return weeklyStreakRepository.findTop20ByOrderByYearDescWeekNumberDesc();
    }

    public WeeklyStreak findWeeklyStreakByDateTime(LocalDateTime date) {
        return weeklyStreakRepository.findByDateWithinRange(date);
    }
}
