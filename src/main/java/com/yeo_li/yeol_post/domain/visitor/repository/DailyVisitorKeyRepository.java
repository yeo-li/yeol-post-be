package com.yeo_li.yeol_post.domain.visitor.repository;

import com.yeo_li.yeol_post.domain.visitor.domain.DailyVisitorKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyVisitorKeyRepository extends JpaRepository<DailyVisitorKey, Long> {

    @Modifying
    @Query(
        value = """
            INSERT IGNORE INTO daily_visitor_key (visit_date, visitor_id, created_at, updated_at)
            VALUES (:visitDate, :visitorId, NOW(6), NOW(6))
            """,
        nativeQuery = true
    )
    int insertIgnore(
        @Param("visitDate") java.sql.Date visitDate,
        @Param("visitorId") String visitorId
    );
}
