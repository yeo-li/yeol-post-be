package com.yeo_li.yeol_post.domain.user.controller;

import com.yeo_li.yeol_post.domain.post.dto.response.UserNicknameAvailabilityResponse;
import com.yeo_li.yeol_post.domain.user.dto.request.UserUpdateRequest;
import com.yeo_li.yeol_post.domain.user.dto.response.UserStatusResponse;
import com.yeo_li.yeol_post.domain.user.service.UserService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "회원 상태/수정/탈퇴 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "현재 사용자 상태 조회", description = "로그인 여부, 닉네임, 온보딩 완료 여부, 역할을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "로그인 상태",
                        value = """
                            {
                              "is_success": true,
                              "code": "GLOBAL200",
                              "message": "성공했습니다.",
                              "result": {
                                "isLoggedIn": true,
                                "nickname": "yeoli",
                                "isOnboardingComplete": true,
                                "role": "USER"
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "비로그인 상태",
                        value = """
                            {
                              "is_success": true,
                              "code": "GLOBAL200",
                              "message": "성공했습니다.",
                              "result": {
                                "isLoggedIn": false,
                                "nickname": null,
                                "isOnboardingComplete": false,
                                "role": null
                              }
                            }
                            """
                    )
                }
            )
        )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserStatusResponse>> getCurrentUser(
        @AuthenticationPrincipal OAuth2User principal) {

        UserStatusResponse response = userService.getUserStatus(principal);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "회원 정보 수정", description = "닉네임, 이메일, 이메일 수신 동의 상태를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "회원 정보 수정 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "nickname": "yeoli",
                  "email": "yeoli@example.com",
                  "emailOptIn": true
                }
                """)
        )
    )
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Object>> updateUser(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody UserUpdateRequest request) {

        userService.updateUser(principal, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "닉네임 사용 가능 여부 확인", description = "입력한 닉네임이 사용 가능한지 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "확인 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": {
                        "duplicated": true
                      }
                    }
                    """)
            )
        )
    })
    @GetMapping("/nickname-availability")
    public ResponseEntity<ApiResponse<UserNicknameAvailabilityResponse>> checkNicknameAvailability(
        @Parameter(description = "확인할 닉네임", example = "yeoli")
        @RequestParam("nickname") String nickname
    ) {
        boolean duplicated = userService.isDuplicatedNickname(nickname.trim());
        return ResponseEntity.ok(
            ApiResponse.onSuccess(new UserNicknameAvailabilityResponse(!duplicated))
        );
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 수행하고 세션을 무효화합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "탈퇴 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
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
