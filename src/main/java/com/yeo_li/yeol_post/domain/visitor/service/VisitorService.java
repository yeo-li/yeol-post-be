package com.yeo_li.yeol_post.domain.visitor.service;

import com.yeo_li.yeol_post.domain.visitor.command.AccessLogCreateCommand;
import com.yeo_li.yeol_post.domain.visitor.dto.response.VisitorResponse;
import com.yeo_li.yeol_post.domain.visitor.facade.AccessLogRepositoryFacade;
import com.yeo_li.yeol_post.domain.visitor.facade.DailyVisitRepositoryFacade;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final AccessLogRepositoryFacade accessLogRepositoryFacade;
    private final DailyVisitRepositoryFacade dailyVisitRepositoryFacade;
    private final VisitorStatsService visitorStatsService;


    // 방문자 접근 기록 수집 기능
    @Transactional
    public void saveAccessLog(AccessLogCreateCommand command) {
        accessLogRepositoryFacade.save(command.toEntity());
        visitorStatsService.saveDailyVisitorKey(command.visitorId());
    }

    // 하루 방문자 수 및 누적 방문자 수 조회 기능
    public VisitorResponse getVisitorCount() {
        Long totalVisitorCount = dailyVisitRepositoryFacade.countTotalCount();
        Long todayVisitorCount = dailyVisitRepositoryFacade.countTodayCount(LocalDate.now());
        return new VisitorResponse(totalVisitorCount, todayVisitorCount);
    }
}
