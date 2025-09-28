package com.yeo_li.yeol_post.streak.repository;

import com.yeo_li.yeol_post.streak.domain.StreakStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreakStatusRepository extends JpaRepository<StreakStatus, Long> {

    StreakStatus findTopByOrderByIdDesc();
}
