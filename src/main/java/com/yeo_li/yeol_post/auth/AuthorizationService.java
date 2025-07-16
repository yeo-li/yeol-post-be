package com.yeo_li.yeol_post.auth;

import com.yeo_li.yeol_post.admin.Admin;
import com.yeo_li.yeol_post.admin.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

  private final AdminRepository adminRepository;

  public void validateAdminAccess(String kakaoId) {
    Admin admin = adminRepository.findByKakaoId(kakaoId)
        .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 관리자입니다."));

    System.out.println(admin.getName() + "님 환영합니다!");
  }

}
