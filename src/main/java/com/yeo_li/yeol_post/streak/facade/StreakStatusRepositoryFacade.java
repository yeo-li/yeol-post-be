package com.yeo_li.yeol_post.streak.facade;

import com.yeo_li.yeol_post.streak.domain.StreakStatus;
import com.yeo_li.yeol_post.streak.repository.StreakStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StreakStatusRepositoryFacade {

    private final StreakStatusRepository streakStatusRepository;

    public StreakStatus findLatest() {
        return streakStatusRepository.findTopByOrderByIdDesc();
    }
}
