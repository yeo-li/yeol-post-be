package com.yeo_li.yeol_post.domain.visitor.repository;

import com.yeo_li.yeol_post.domain.visitor.domain.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {


}
