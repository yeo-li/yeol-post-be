package com.yeo_li.yeol_post.admin.repository;

import com.yeo_li.yeol_post.admin.domain.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByKakaoId(String kakaoId);

  Admin findAdminById(Long id);
}
