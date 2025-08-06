package com.yeo_li.yeol_post.admin.repository;

import com.yeo_li.yeol_post.admin.domain.Introduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntroductionRepository extends JpaRepository<Introduction, Long> {

}
