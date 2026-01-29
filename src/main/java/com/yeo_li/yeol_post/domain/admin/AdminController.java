package com.yeo_li.yeol_post.domain.admin;

import com.yeo_li.yeol_post.domain.admin.domain.Admin;
import com.yeo_li.yeol_post.domain.admin.dto.AdminPasswordUpdateRequest;
import com.yeo_li.yeol_post.domain.admin.dto.AdminUpdateRequest;
import com.yeo_li.yeol_post.domain.auth.AuthorizationService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AuthorizationService authorizationService;
    private final AdminService adminService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                    "isLoggedIn", false
                ));
        }

        Map<String, Object> attributes = principal.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

        Admin admin = adminService.findAdminByKakaoId(String.valueOf(attributes.get("id")));

        return ResponseEntity.ok(Map.of(
            "nickname", nickname,
            "admin_id", admin.getId(),
            "isLoggedIn", true
        ));
    }

    @PatchMapping("/{adminId}")
    public ResponseEntity<ApiResponse<Object>> updateAdmin(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long adminId,
        @RequestBody AdminUpdateRequest request) {

        // 인증, 인가 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

        adminService.updateAdmin(adminId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @PatchMapping("/{adminId}/password")
    public ResponseEntity<ApiResponse<Object>> updatePassword(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long adminId,
        @RequestBody AdminPasswordUpdateRequest request) {

        // 인증, 인가 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

        adminService.updateAdminPassword(adminId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }
}
