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
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;

    public User findUserByKakaoId(String kakaoId) {
        return userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
    }

    public boolean isDuplicatedNickname(String nickname) {
        User duplicatedUser = userRepository.findUserByNicknameAndDeletedAtIsNull(nickname);
        return duplicatedUser != null;

    }

    @Transactional
    public void updateUser(OAuth2User principal, UserUpdateRequest request) {
        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
        }

        UserUpdateRequest normalizedRequest = normalizeUserUpdateRequest(request);
        validateUserUpdateRequest(user, normalizedRequest);

        if (user.getOnboardingCompletedAt() == null) {
            initUser(user, normalizedRequest);
            return;
        }

        if (normalizedRequest.nickname() != null) {
            user.setNickname(normalizedRequest.nickname());
        }
        if (normalizedRequest.email() != null) {
            user.setEmail(normalizedRequest.email());
        }
        if (normalizedRequest.emailOptIn() != null) {
            user.setEmailOptIn(normalizedRequest.emailOptIn());
        }
    }

    @Transactional
    public void initUser(User user, UserUpdateRequest request) {
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
        if (attributes.get("id") == null) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ID_MISSING);
        }
        return String.valueOf(attributes.get("id"));
    }

    private UserUpdateRequest normalizeUserUpdateRequest(UserUpdateRequest request) {
        if (request == null) {
            return null;
        }

        String normalizedNickname = request.nickname() == null ? null : request.nickname().trim();
        String normalizedEmail = request.email() == null ? null : request.email().trim();

        return new UserUpdateRequest(normalizedNickname, normalizedEmail, request.emailOptIn());
    }

    private void validateUserUpdateRequest(User user, UserUpdateRequest request) {
        if (request == null) {
            throw new GeneralException(UserExceptionType.USER_UPDATE_INVALID);
        }

        if (request.email() != null) {
            String email = request.email().trim();
            if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
                throw new GeneralException(UserExceptionType.USER_EMAIL_INVALID);
            }
        }

        if (request.nickname() != null) {
            String nickname = request.nickname().trim();
            if (nickname.isEmpty() || nickname.length() > 10) {
                throw new GeneralException(UserExceptionType.USER_NICKNAME_INVALID);
            }

            User duplicatedUser = userRepository.findUserByNicknameAndDeletedAtIsNull(nickname);
            if (duplicatedUser != null && !duplicatedUser.getId().equals(user.getId())) {
                throw new GeneralException(UserExceptionType.USER_NICKNAME_DUPLICATED);
            }
        }
    }

    @Transactional
    public void deleteUser(OAuth2User principal) {
        String kakaoId = getKakaoId(principal);

        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(UserExceptionType.USER_NOT_FOUND);
        }

        user.setDeletedAt(LocalDateTime.now());
    }

}
