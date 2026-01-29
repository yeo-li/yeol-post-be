package com.yeo_li.yeol_post.domain.visitor.facade;

import com.yeo_li.yeol_post.domain.visitor.domain.DailyVisitorKey;
import com.yeo_li.yeol_post.domain.visitor.repository.DailyVisitorKeyRepository;
import java.sql.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyVisitorKeyRepositoryFacade {

    private final DailyVisitorKeyRepository dailyVisitorKeyRepository;

    public void save(DailyVisitorKey dailyVisitorKey) {
        dailyVisitorKeyRepository.save(dailyVisitorKey);
    }

    public void saveAndFlush(DailyVisitorKey dailyVisitorKey) {
        dailyVisitorKeyRepository.saveAndFlush(dailyVisitorKey);
    }

    public int insertIgnore(java.time.LocalDate visitDate, java.util.UUID visitorId) {
        return dailyVisitorKeyRepository.insertIgnore(
            Date.valueOf(visitDate),
            visitorId.toString()
        );
    }
}
