package com.yeo_li.yeol_post.domain.user;

import com.yeo_li.yeol_post.domain.auth.AuthorizationService;
import com.yeo_li.yeol_post.domain.post.dto.response.UserNicknameAvailabilityResponse;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.dto.UserUpdateRequest;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthorizationService authorizationService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
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

        authorizationService.validateUserAccess(String.valueOf(attributes.get("id")));

        User user = userService.findUserByKakaoId(String.valueOf(attributes.get("id")));

        return ResponseEntity.ok(Map.of(
            "nickname", nickname,
            "user_id", user.getId(),
            "isLoggedIn", true
        ));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Object>> updateUser(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody UserUpdateRequest request) {

        userService.updateUser(principal, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @GetMapping("/nickname-availability")
    public ResponseEntity<ApiResponse<UserNicknameAvailabilityResponse>> checkNicknameAvailability(
        @RequestParam("nickname") String nickname
    ) {
        boolean duplicated = userService.isDuplicatedNickname(nickname.trim());
        return ResponseEntity.ok(
            ApiResponse.onSuccess(new UserNicknameAvailabilityResponse(!duplicated))
        );
    }
}
