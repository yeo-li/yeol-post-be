package com.yeo_li.yeol_post.domain.user.service;

import com.yeo_li.yeol_post.domain.subscription.domain.Subscription;
import com.yeo_li.yeol_post.domain.subscription.domain.SubscriptionStatus;
import com.yeo_li.yeol_post.domain.subscription.service.NewsLetterService;
import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import com.yeo_li.yeol_post.domain.user.domain.Role;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.dto.request.UserUpdateRequest;
import com.yeo_li.yeol_post.domain.user.dto.response.UserProfileResponse;
import com.yeo_li.yeol_post.domain.user.dto.response.UserStatusResponse;
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
    private final KakaoUnlinkService kakaoUnlinkService;
    private final SubscriptionService subscriptionService;
    private final NewsLetterService newsLetterService;

    public boolean isDuplicatedNickname(OAuth2User principal, String nickname) {
        String kakaoId = getKakaoId(principal);
        if (kakaoId == null) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ID_MISSING);
        }

        User duplicatedUser = userRepository.findUserByNicknameAndDeletedAtIsNull(nickname);
        return duplicatedUser != null && !duplicatedUser.getKakaoId().equals(kakaoId);

    }

    @Transactional
    public void updateUser(OAuth2User principal, UserUpdateRequest request) {
        String kakaoId = getKakaoId(principal);
        if (kakaoId == null) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ID_MISSING);
        }
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
            if (isDuplicatedNickname(principal, normalizedRequest.nickname())) {
                throw new GeneralException(UserExceptionType.USER_NICKNAME_DUPLICATED);
            }
            user.setNickname(normalizedRequest.nickname());
        }

        String currentEmail = user.getEmail();
        String requestEmail = normalizedRequest.email();
        String targetEmail = currentEmail;
        if (requestEmail != null) {
            targetEmail = requestEmail;
        }
        boolean isEmailChanged = requestEmail != null && !requestEmail.equals(currentEmail);
        Subscription subscription = null;
        if (targetEmail != null) {
            subscription = subscriptionService.getSubscriptionByEmail(targetEmail);
        }

        if (isEmailChanged) {
            // 기존 이메일 구독 해제
            if (currentEmail != null) {
                Subscription unsubscription = subscriptionService.getSubscriptionByEmail(
                    currentEmail);
                if (unsubscription != null) {
                    unsubscription.setUser(null);
                    // 구독 상태라면 구독 해제 발송
                    if (unsubscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE) {
                        markAsUnsubscribed(unsubscription);
                        newsLetterService.sendUnsubscribedNotification(unsubscription);
                    }
                }
            }

            // 변경한 이메일 등록 또는 활성화
            if (subscription == null) {
                // 없는건 이렇게 두기
                subscription = subscriptionService.saveSubscription(requestEmail);
                markAsUnsubscribed(subscription);
            }

            subscription.setUser(user);
            user.setEmail(requestEmail);
        }

        if (normalizedRequest.emailOptIn() != null) {
            if (targetEmail == null) {
                throw new GeneralException(UserExceptionType.USER_EMAIL_INVALID);
            }

            if (subscription == null) {
                subscription = subscriptionService.saveSubscription(targetEmail);
                markAsUnsubscribed(subscription);
                subscription.setUser(user);
            }

            if (normalizedRequest.emailOptIn()
                && subscription.getSubscriptionStatus() == SubscriptionStatus.UNSUBSCRIBE) {
                subscriptionService.subscribe(targetEmail);
            }
            if (!normalizedRequest.emailOptIn()
                && subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE) {
                markAsUnsubscribed(subscription);
                newsLetterService.sendUnsubscribedNotification(subscription);
            }
        }
    }

    @Transactional
    public void initUser(User user, UserUpdateRequest request) {
        if (request.nickname() == null || request.email() == null || request.emailOptIn() == null) {
            throw new GeneralException(UserExceptionType.USER_ONBOARDING_INVALID);
        }

        UserUpdateRequest normalizedRequest = normalizeUserUpdateRequest(request);
        Subscription subscription = subscriptionService.getSubscriptionByEmail(
            normalizedRequest.email());

        // 변경한 이메일 등록 또는 활성화
        if (subscription == null) {
            // 없는건 이렇게 두기
            subscription = subscriptionService.saveSubscription(normalizedRequest.email());
            markAsUnsubscribed(subscription);
        }

        subscription.setUser(user);

        if (normalizedRequest.emailOptIn()
            && subscription.getSubscriptionStatus() == SubscriptionStatus.UNSUBSCRIBE) {
            subscriptionService.subscribe(normalizedRequest.email());
        }
        if (!normalizedRequest.emailOptIn()
            && subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE) {
            markAsUnsubscribed(subscription);
            newsLetterService.sendUnsubscribedNotification(subscription);
        }

        user.setNickname(normalizedRequest.nickname());
        user.setEmail(normalizedRequest.email());
        user.setOnboardingCompletedAt(LocalDateTime.now());
    }

    private String getKakaoId(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes.get("id") == null) {
            return null;
        }
        return String.valueOf(attributes.get("id"));
    }

    private void markAsUnsubscribed(Subscription subscription) {
        subscription.setSubscriptionStatus(SubscriptionStatus.UNSUBSCRIBE);
        subscription.setUnsubscribedAt(LocalDateTime.now());
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
    public void deleteUser(OAuth2User principal, String kakaoAccessToken) {
        String kakaoId = getKakaoId(principal);
        if (kakaoId == null) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ID_MISSING);
        }
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(UserExceptionType.USER_NOT_FOUND);
        }

        Subscription subscription = subscriptionService.getSubscriptionByEmail(user.getEmail());
        if (subscription != null) {
            subscription.setUser(null);
            if (subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE) {
                markAsUnsubscribed(subscription);
                newsLetterService.sendUnsubscribedNotification(subscription);
            }
        }

        kakaoUnlinkService.unlink(kakaoAccessToken);
        user.setRole(Role.USER);
        user.setName("알수없음");
        user.setNickname(null);
        user.setEmail(null);
        user.setOnboardingCompletedAt(null);
        user.setDeletedAt(LocalDateTime.now());
    }

    public UserStatusResponse getUserStatus(OAuth2User principal) {
        if (principal == null) {
            return new UserStatusResponse(false, null, false, null);
        }

        String kakaoId = getKakaoId(principal);
        if (kakaoId == null) {
            return new UserStatusResponse(false, null, false, null);
        }

        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            return new UserStatusResponse(false, null, false, null);
        }
        boolean isOnboardingComplete = user.getOnboardingCompletedAt() != null;
        return new UserStatusResponse(true, user.getNickname(), isOnboardingComplete,
            user.getRole());
    }

    public UserProfileResponse getUserProfile(OAuth2User principal) {
        if (principal == null) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ID_MISSING);
        }

        String kakaoId = getKakaoId(principal);
        if (kakaoId == null) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ID_MISSING);
        }

        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(UserExceptionType.USER_NOT_FOUND);
        }

        Subscription subscription = null;
        if (user.getEmail() != null) {
            subscription = subscriptionService.getSubscriptionByEmail(user.getEmail());
        }
        boolean isSubscribed =
            subscription != null
                && subscription.getSubscriptionStatus() == SubscriptionStatus.SUBSCRIBE;

        return new UserProfileResponse(
            user.getName(),
            user.getNickname(),
            user.getEmail(),
            isSubscribed
        );
    }

}
