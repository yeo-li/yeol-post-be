package com.yeo_li.yeol_post.admin;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final AdminRepository adminRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // 기본 사용자 정보 조회
    OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

    // 카카오 정보 꺼내기
    Map<String, Object> attributes = oauth2User.getAttributes();
    String kakaoId = (String) attributes.get("id");

    // 우리 DB에 등록된 사용자 찾기
    Optional<Admin> admin = adminRepository.findByKakaoId(kakaoId);

    if (admin.isEmpty()) {
      throw new OAuth2AuthenticationException("가입된 관리자가 아닙니다.");
    }

    // CustomUserDetails 또는 그냥 oauth2User 반환 가능
    return oauth2User;
  }
}
