package com.yeo_li.yeol_post.domain.user;

import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.dto.UserUpdateRequest;
import com.yeo_li.yeol_post.domain.user.exception.UserExceptionType;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserByKakaoId(String kakaoId) {
        return userRepository.findUserByKakaoId(kakaoId);
    }

    @Transactional
    public void updateUser(OAuth2User principal, UserUpdateRequest request) {
        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoId(kakaoId);
        if (user == null) {
            throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
        }

        if (user.getOnboardingCompletedAt() == null) {
            initUser(principal, request);
            return;
        }

        if (request.nickname() != null) {
            user.setNickname(request.nickname());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.emailOptIn() != null) {
            user.setEmailOptIn(request.emailOptIn());
        }
    }

    @Transactional
    public void initUser(OAuth2User principal, UserUpdateRequest request) {
        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoId(kakaoId);
        if (user == null) {
            throw new GeneralException(UserExceptionType.USER_NOT_FOUND);
        }

        if (request.nickname() == null || request.email() == null || request.emailOptIn() == null) {
            throw new GeneralException(UserExceptionType.USER_ONBOARDING_INVALID);
        }

        user.setNickname(request.nickname());
        user.setEmail(request.email());
        user.setEmailOptIn(request.emailOptIn());
        user.setOnboardingCompletedAt(LocalDateTime.now());
    }

    private String getKakaoId(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        return String.valueOf(attributes.get("id"));
    }

}
