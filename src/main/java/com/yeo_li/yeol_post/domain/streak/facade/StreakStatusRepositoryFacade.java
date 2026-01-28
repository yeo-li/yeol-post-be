package com.yeo_li.yeol_post.domain.streak.facade;

import com.yeo_li.yeol_post.domain.streak.domain.StreakStatus;
import com.yeo_li.yeol_post.domain.streak.repository.StreakStatusRepository;
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

    public void save(StreakStatus streakStatus) {
        streakStatusRepository.save(streakStatus);
    }

    public void delete(StreakStatus streakStatus) {
        streakStatusRepository.delete(streakStatus);
    }
}
