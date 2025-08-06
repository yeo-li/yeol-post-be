package com.yeo_li.yeol_post.auth;

import com.yeo_li.yeol_post.admin.domain.Admin;
import com.yeo_li.yeol_post.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final AdminRepository adminRepository;

    public void validateAdminAccess(String kakaoId) {
        Admin admin = adminRepository.findByKakaoId(kakaoId)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 관리자입니다."));
    }

}
