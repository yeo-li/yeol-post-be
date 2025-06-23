package com.yeo_li.yeol_post.admin;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByKakaoId(String kakaoId);
}
