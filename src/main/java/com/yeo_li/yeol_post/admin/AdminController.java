package com.yeo_li.yeol_post.admin;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal,
      HttpServletRequest request) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Cookie[] cookies = request.getCookies();
    Arrays.stream(cookies).forEach(cookie -> {
      System.out.println("쿠키 이름: " + cookie.getName() + ", 값: " + cookie.getValue());
    });
    Map<String, Object> attributes = principal.getAttributes();
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    String nickname = (String) profile.get("nickname");
    return ResponseEntity.ok(Map.of(
        "nickname", nickname,
        "isLoggedIn", true
    ));
  }

  @GetMapping("/csrf")
  public CsrfToken csrf(CsrfToken token) {
    return token; // JSON 형태로 token.value 내려줌
  }
}
