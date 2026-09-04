package com.yeo_li.yeol_post.domain.visitor.service;

import com.yeo_li.yeol_post.domain.visitor.command.AccessLogCreateCommand;
import com.yeo_li.yeol_post.domain.visitor.domain.AccessLog;
import com.yeo_li.yeol_post.domain.visitor.dto.response.VisitorResponse;
import com.yeo_li.yeol_post.domain.visitor.facade.AccessLogRepositoryFacade;
import com.yeo_li.yeol_post.domain.visitor.facade.DailyVisitRepositoryFacade;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorService {

    private final AccessLogRepositoryFacade accessLogRepositoryFacade;
    private final DailyVisitRepositoryFacade dailyVisitRepositoryFacade;
    private final VisitorStatsService visitorStatsService;


    // 방문자 접근 기록 수집 기능
    @Transactional
    public void saveAccessLog(AccessLogCreateCommand command) {
        AccessLog accessLog = command.toEntity();
        accessLogRepositoryFacade.save(accessLog);

        visitorStatsService.saveDailyVisitorKey(command.visitorId());

        log.info(StructuredLog.event(
                "ACCESS_LOG_CREATED",
                "방문자 접근 기록이 생성되었습니다.",
                "CREATED"
            )
            .field("accessLogId", accessLog.getId())
            .field("visitorId", command.visitorId())
            .field("osType", command.osType())
            .field("browserType", command.browserType())
            .build());
    }

    // 하루 방문자 수 및 누적 방문자 수 조회 기능
    public VisitorResponse getVisitorCount() {
        Long totalVisitorCount = dailyVisitRepositoryFacade.countTotalCount();
        Long todayVisitorCount = dailyVisitRepositoryFacade.countTodayCount(LocalDate.now());
        return new VisitorResponse(totalVisitorCount, todayVisitorCount);
    }
}
