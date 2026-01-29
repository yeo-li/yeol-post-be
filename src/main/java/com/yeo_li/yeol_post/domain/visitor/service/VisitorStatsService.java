package com.yeo_li.yeol_post.domain.visitor.service;

import com.yeo_li.yeol_post.domain.visitor.domain.DailyVisit;
import com.yeo_li.yeol_post.domain.visitor.facade.DailyVisitRepositoryFacade;
import com.yeo_li.yeol_post.domain.visitor.facade.DailyVisitorKeyRepositoryFacade;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitorStatsService {

    private final DailyVisitRepositoryFacade dailyVisitRepositoryFacade;
    private final DailyVisitorKeyRepositoryFacade dailyVisitorKeyRepositoryFacade;

    // 하루 방문자 집계 기능 (외부 트랜잭션과 분리)
    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        noRollbackFor = org.springframework.dao.DataIntegrityViolationException.class
    )
    public void saveDailyVisitorKey(UUID visitorId) {
        LocalDate now = LocalDate.now();

        int inserted = dailyVisitorKeyRepositoryFacade.insertIgnore(now, visitorId);

        if (inserted > 0) {
            updateVisitorCount(now);
        }
    }

    // 하루 방문자 수 집계 기능 (REQUIRES_NEW 트랜잭션 내부)
    private void updateVisitorCount(LocalDate visitDate) {
        DailyVisit dailyVisit = dailyVisitRepositoryFacade.findDailyVisitByVisitDate(visitDate);

        if (dailyVisit == null) {
            DailyVisit createdDailyVisit = new DailyVisit();
            createdDailyVisit.setVisitCount(1L);
            createdDailyVisit.setVisitDate(visitDate);
            dailyVisitRepositoryFacade.saveAndFlush(createdDailyVisit);
            return;
        }

        dailyVisit.setVisitCount(dailyVisit.getVisitCount() + 1);
        dailyVisitRepositoryFacade.saveAndFlush(dailyVisit);
    }
}
