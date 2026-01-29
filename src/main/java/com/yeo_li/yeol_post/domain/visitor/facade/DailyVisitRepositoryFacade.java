package com.yeo_li.yeol_post.domain.visitor.facade;

import com.yeo_li.yeol_post.domain.visitor.domain.DailyVisit;
import com.yeo_li.yeol_post.domain.visitor.repository.DailyVisitRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyVisitRepositoryFacade {

    private final DailyVisitRepository dailyVisitRepository;

    public Long countTotalCount() {
        Long totalCount = 0L;
        List<DailyVisit> dailyVisits = dailyVisitRepository.findAll();
        for (DailyVisit dailyVisit : dailyVisits) {
            totalCount += dailyVisit.getVisitCount();
        }
        return totalCount;
    }

    public Long countTodayCount(LocalDate visitDate) {
        DailyVisit dailyVisits = dailyVisitRepository.findDailyVisitByVisitDate(visitDate);
        if (dailyVisits == null) {
            return 0L;
        }
        return dailyVisits.getVisitCount();
    }

    public DailyVisit findDailyVisitByVisitDate(LocalDate visitDate) {
        return dailyVisitRepository.findDailyVisitByVisitDate(visitDate);
    }

    public void save(DailyVisit dailyVisit) {
        dailyVisitRepository.save(dailyVisit);
    }

    public void saveAndFlush(DailyVisit dailyVisit) {
        dailyVisitRepository.saveAndFlush(dailyVisit);
    }

}
