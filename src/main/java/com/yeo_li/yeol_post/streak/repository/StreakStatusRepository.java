package com.yeo_li.yeol_post.streak.repository;

import com.yeo_li.yeol_post.streak.domain.StreakStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StreakStatusRepository extends JpaRepository<StreakStatus, Long> {

    @Query("SELECT s FROM StreakStatus s ORDER BY s.createdAt DESC")
    StreakStatus findLatest();
}
