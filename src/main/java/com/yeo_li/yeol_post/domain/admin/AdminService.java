package com.yeo_li.yeol_post.domain.admin;

import com.yeo_li.yeol_post.domain.admin.domain.Admin;
import com.yeo_li.yeol_post.domain.admin.dto.AdminPasswordUpdateRequest;
import com.yeo_li.yeol_post.domain.admin.dto.AdminUpdateRequest;
import com.yeo_li.yeol_post.domain.admin.repository.AdminRepository;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.handler.AdminHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Admin findAdminByKakaoId(String kakaoId) {
        return adminRepository.findAdminByKakaoId(kakaoId);
    }

    @Transactional
    public void updateAdmin(Long adminId, AdminUpdateRequest request) {
        Admin admin = adminRepository.findAdminById(adminId);

        if (request.name() != null) {
            admin.setName(request.name());
        }
        if (request.nickname() != null) {
            admin.setNickname(request.nickname());
        }
        if (request.username() != null) {
            admin.setUsername(request.username());
        }
        if (request.email() != null) {
            admin.setEmail(request.email());
        }
    }

    @Transactional
    public void updateAdminPassword(Long adminId, AdminPasswordUpdateRequest request) {
        Admin admin = adminRepository.findAdminById(adminId);

        if (request.currentPassword().isBlank() || !request.currentPassword()
            .equals(admin.getPassword())) {
            throw new AdminHandler(ErrorStatus.VALIDATION_ERROR);
        }

        if (request.newPassword().isBlank()) {
            return;
        }

        admin.setPassword(request.newPassword());
    }
}
