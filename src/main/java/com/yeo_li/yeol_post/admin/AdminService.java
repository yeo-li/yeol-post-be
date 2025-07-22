package com.yeo_li.yeol_post.admin;

import com.yeo_li.yeol_post.admin.domain.Admin;
import com.yeo_li.yeol_post.admin.dto.AdminUpdateRequest;
import com.yeo_li.yeol_post.admin.repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final AdminRepository adminRepository;

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
}
