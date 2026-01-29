package com.yeo_li.yeol_post.domain.streak.repository;

import com.yeo_li.yeol_post.domain.streak.domain.StreakStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreakStatusRepository extends JpaRepository<StreakStatus, Long> {

    StreakStatus findTopByOrderByIdDesc();
}
