package com.yeo_li.yeol_post.domain.user.service;

import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.exception.UserExceptionType;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 사용자 정보 조회
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 카카오 정보 꺼내기
        if (oauth2User.getAttributes().get("id") == null) {
            throw new OAuth2AuthenticationException(
                UserExceptionType.USER_OAUTH2_ID_MISSING.getMessage());
        }
        String kakaoId = String.valueOf(oauth2User.getAttributes().get("id"));

        // 우리 DB에 등록된 사용자 찾기
        User user = userRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId)
            .orElseThrow(() -> new OAuth2AuthenticationException("가입된 사용자가 아닙니다."));

        Collection<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();

        // CustomUserDetails 또는 그냥 oauth2User 반환 가능
        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(),
            userNameAttributeName);
    }
}
