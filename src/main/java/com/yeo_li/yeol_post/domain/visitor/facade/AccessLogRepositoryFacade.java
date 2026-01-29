package com.yeo_li.yeol_post.domain.visitor.facade;

import com.yeo_li.yeol_post.domain.visitor.domain.AccessLog;
import com.yeo_li.yeol_post.domain.visitor.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccessLogRepositoryFacade {

    private final AccessLogRepository accessLogRepository;

    @Transactional
    public void save(AccessLog accessLog) {
        accessLogRepository.save(accessLog);
    }
}
