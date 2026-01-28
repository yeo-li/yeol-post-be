package com.yeo_li.yeol_post.domain.visitor.repository;

import com.yeo_li.yeol_post.domain.visitor.domain.DailyVisit;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyVisitRepository extends JpaRepository<DailyVisit, Long> {

    List<DailyVisit> findAll();

    DailyVisit findDailyVisitByVisitDate(LocalDate visitDate);
}
