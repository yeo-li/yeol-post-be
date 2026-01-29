package com.yeo_li.yeol_post.domain.streak.facade;

import com.yeo_li.yeol_post.domain.streak.domain.WeeklyStreak;
import com.yeo_li.yeol_post.domain.streak.repository.WeeklyStreakRepository;
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

    public void save(WeeklyStreak weeklyStreak) {
        weeklyStreakRepository.save(weeklyStreak);
    }
}
