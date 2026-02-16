package com.yeo_li.yeol_post.domain.auth;

import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    public void validateUserAccess(String kakaoId) {
        User user = userRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));
    }

}
