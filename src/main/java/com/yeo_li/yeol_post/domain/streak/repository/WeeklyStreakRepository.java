package com.yeo_li.yeol_post.domain.streak.repository;

import com.yeo_li.yeol_post.domain.streak.domain.WeeklyStreak;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyStreakRepository extends JpaRepository<WeeklyStreak, Long> {

    @Query("SELECT w FROM WeeklyStreak w " +
        "WHERE w.startDateTime <= :date " +
        "AND w.endDateTime > :date")
    WeeklyStreak findByDateWithinRange(@Param("date") LocalDateTime date);

    List<WeeklyStreak> findTop20ByOrderByYearDescWeekNumberDesc();
}
