package com.yeo_li.yeol_post.domain.streak.service;

import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.streak.domain.StreakStatus;
import com.yeo_li.yeol_post.domain.streak.domain.WeeklyStreak;
import com.yeo_li.yeol_post.domain.streak.dto.response.StreakResponse;
import com.yeo_li.yeol_post.domain.streak.exception.StreakExceptionType;
import com.yeo_li.yeol_post.domain.streak.facade.StreakStatusRepositoryFacade;
import com.yeo_li.yeol_post.domain.streak.facade.WeeklyStreakRepositoryFacade;
import com.yeo_li.yeol_post.domain.streak.repository.WeeklyStreakRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreakService {

    private final WeeklyStreakRepositoryFacade weeklyStreakRepositoryFacade;
    private final StreakStatusRepositoryFacade streakStatusRepositoryFacade;
    private final WeeklyStreakRepository weeklyStreakRepository;


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

        log.info(StructuredLog.event(
                "WEEKLY_STREAK_POST_COUNT_INCREASED",
                "주간 스트릭 게시물 수가 증가했습니다.",
                "INCREMENTED"
            )
            .field("weeklyStreakId", streak.getId())
            .field("year", streak.getYear())
            .field("weekNumber", streak.getWeekNumber())
            .field("postCount", streak.getPostCount())
            .build());

        if (streak.getPostCount() == 1) {
            createStreakStatus(dateTime);
        }
    }

    @Transactional
    public void createWeeklyStreak(LocalDateTime date) {
        LocalDateTime startDateTime = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(LocalTime.MIN).plusHours(6);

        WeeklyStreak streak = WeeklyStreak.builder()
            .year(getYear(date))
            .weekNumber(getWeekNumber(date))
            .startDateTime(startDateTime)
            .endDateTime(startDateTime.plusWeeks(1).minusSeconds(1))
            .postCount(0)
            .build();

        weeklyStreakRepository.save(streak);

        log.info(StructuredLog.event(
                "WEEKLY_STREAK_CREATED",
                "주간 스트릭이 생성되었습니다.",
                "CREATED"
            )
            .field("weeklyStreakId", streak.getId())
            .field("year", streak.getYear())
            .field("weekNumber", streak.getWeekNumber())
            .build());
    }

    @Transactional
    public void removeStreakCount(Post post) {
        if (!isThisWeek(post.getPublishedAt())) {
            return;
        }
        WeeklyStreak streak = weeklyStreakRepositoryFacade.findWeeklyStreakByDateTime(
            post.getPublishedAt());

        streak.removePostCount();

        log.info(StructuredLog.event(
                "WEEKLY_STREAK_POST_COUNT_DECREASED",
                "주간 스트릭 게시물 수가 감소했습니다.",
                "DECREMENTED"
            )
            .field("weeklyStreakId", streak.getId())
            .field("year", streak.getYear())
            .field("weekNumber", streak.getWeekNumber())
            .field("postCount", streak.getPostCount())
            .field("postId", post.getId())
            .build());

        if (streak.getPostCount() <= 0) {
            removeStreakStatus();
        }
    }

    private boolean isThisWeek(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now().minusHours(6);

        LocalDateTime monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(LocalTime.MIN);

        LocalDateTime startOfWeek = monday.plusHours(6);

        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1).minusSeconds(1);

        return !dateTime.isBefore(startOfWeek) && !dateTime.isAfter(endOfWeek);
    }

    @Transactional
    public void createStreakStatus(LocalDateTime date) {
        StreakStatus latestStreakStatus = streakStatusRepositoryFacade.findLatest();

        StreakStatus streakStatus = StreakStatus.builder()
            .year(getYear(date))
            .weekNumber(getWeekNumber(date))
            .currentStreakLength(latestStreakStatus.getCurrentStreakLength() + 1)
            .maxStreakLength(Math.max(latestStreakStatus.getMaxStreakLength(),
                latestStreakStatus.getCurrentStreakLength() + 1))
            .build();

        streakStatusRepositoryFacade.save(streakStatus);

        log.info(StructuredLog.event(
                "STREAK_STATUS_CREATED",
                "스트릭 상태가 생성되었습니다.",
                "CREATED"
            )
            .field("year", streakStatus.getYear())
            .field("weekNumber", streakStatus.getWeekNumber())
            .field("currentStreakLength", streakStatus.getCurrentStreakLength())
            .field("maxStreakLength", streakStatus.getMaxStreakLength())
            .build());
    }

    @Transactional
    public void createInitStreakStatus(LocalDateTime date) {
        StreakStatus latestStreakStatus = streakStatusRepositoryFacade.findLatest();

        StreakStatus streakStatus = StreakStatus.builder()
            .year(getYear(date))
            .weekNumber(getWeekNumber(date))
            .currentStreakLength(0)
            .maxStreakLength(latestStreakStatus.getMaxStreakLength())
            .build();

        streakStatusRepositoryFacade.save(streakStatus);

        log.info(StructuredLog.event(
                "STREAK_STATUS_INITIALIZED",
                "스트릭 상태가 초기화되었습니다.",
                "INITIALIZED"
            )
            .field("year", streakStatus.getYear())
            .field("weekNumber", streakStatus.getWeekNumber())
            .field("currentStreakLength", streakStatus.getCurrentStreakLength())
            .field("maxStreakLength", streakStatus.getMaxStreakLength())
            .build());
    }

    public void removeStreakStatus() {
        StreakStatus streakStatus = streakStatusRepositoryFacade.findLatest();
        streakStatusRepositoryFacade.delete(streakStatus);

        log.info(StructuredLog.event(
                "STREAK_STATUS_REMOVED",
                "스트릭 상태가 삭제되었습니다.",
                "DELETED"
            )
            .field("year", streakStatus.getYear())
            .field("weekNumber", streakStatus.getWeekNumber())
            .field("currentStreakLength", streakStatus.getCurrentStreakLength())
            .field("maxStreakLength", streakStatus.getMaxStreakLength())
            .build());
    }

    @Transactional
    public void updateStreakStatus(LocalDateTime dateTime) {

        StreakStatus status = streakStatusRepositoryFacade.findLatest(); //
        WeeklyStreak streak = weeklyStreakRepositoryFacade.findWeeklyStreakByDateTime(
            dateTime.minusDays(1));

        if (streak.getWeekNumber() == status.getWeekNumber()
            && streak.getYear() == status.getYear()) {
            return;
        }

        createInitStreakStatus(dateTime.minusDays(1));
    }

    private int getWeekNumber(LocalDateTime dateTime) {
        LocalDateTime thisWeek = dateTime.minusHours(6);

        LocalDateTime monday = thisWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(LocalTime.MIN);

        LocalDateTime startOfWeek = monday.plusHours(6);
        WeekFields weekFields = WeekFields.ISO;

        return startOfWeek.get(weekFields.weekOfYear());
    }

    private int getYear(LocalDateTime dateTime) {
        LocalDateTime thisWeek = dateTime.minusHours(6);

        LocalDateTime monday = thisWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(LocalTime.MIN);

        LocalDateTime startOfWeek = monday.plusHours(6);
        WeekFields weekFields = WeekFields.ISO;

        return startOfWeek.get(weekFields.weekBasedYear());
    }
}
