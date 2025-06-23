package com.yeo_li.yeol_post.admin;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

  @GetMapping("/me")
  public ResponseEntity<?> me(@AuthenticationPrincipal OAuth2User oAuth2User) {
    if (oAuth2User == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "로그인이 필요합니다."));
    }
    System.out.println(oAuth2User.getAttributes().toString());
    return ResponseEntity.ok(oAuth2User.getAttributes());
  }
}
