package com.yeo_li.yeol_post.domain.user.controller;

import com.yeo_li.yeol_post.domain.post.dto.response.UserNicknameAvailabilityResponse;
import com.yeo_li.yeol_post.domain.user.dto.request.UserUpdateRequest;
import com.yeo_li.yeol_post.domain.user.dto.response.UserStatusResponse;
import com.yeo_li.yeol_post.domain.user.service.UserService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserStatusResponse>> getCurrentUser(
        @AuthenticationPrincipal OAuth2User principal) {

        UserStatusResponse response = userService.getUserStatus(principal);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
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

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Object>> deleteUser(
        @AuthenticationPrincipal OAuth2User principal,
        @RegisteredOAuth2AuthorizedClient("kakao") OAuth2AuthorizedClient authorizedClient,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException {
        String kakaoAccessToken = authorizedClient == null || authorizedClient.getAccessToken() == null
            ? null
            : authorizedClient.getAccessToken().getTokenValue();
        userService.deleteUser(principal, kakaoAccessToken);

        request.logout();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

}
