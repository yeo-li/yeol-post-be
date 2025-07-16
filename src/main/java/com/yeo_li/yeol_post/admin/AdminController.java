package com.yeo_li.yeol_post.admin;

import com.yeo_li.yeol_post.auth.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AuthorizationService authorizationService;

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal OAuth2User principal,
      HttpServletRequest request) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Map<String, Object> attributes = principal.getAttributes();
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    String nickname = (String) profile.get("nickname");

    authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

    return ResponseEntity.ok(Map.of(
        "nickname", nickname,
        "isLoggedIn", true
    ));
  }
}
