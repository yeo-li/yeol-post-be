package com.yeo_li.yeol_post.domain.visitor.controller;

import com.yeo_li.yeol_post.domain.visitor.command.AccessLogCreateCommand;
import com.yeo_li.yeol_post.domain.visitor.dto.VisitorCommandFactory;
import com.yeo_li.yeol_post.domain.visitor.dto.response.VisitorResponse;
import com.yeo_li.yeol_post.domain.visitor.service.VisitorService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/visitors")
@RequiredArgsConstructor
@Tag(name = "Visitor", description = "방문자 통계 API")
public class VisitorController {

    private final VisitorService visitorService;
    private final VisitorCommandFactory visitorCommandFactory;


    @Operation(summary = "방문 기록 저장", description = "접속 페이지 정보를 기반으로 방문 로그를 저장합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "저장 성공",
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
    @GetMapping("/access")
    public ResponseEntity<ApiResponse<Void>> accessVisitor(
        @Parameter(hidden = true)
        HttpServletRequest request,
        @Parameter(description = "현재 방문한 페이지 URL", example = "https://yeol-post.com/posts/10")
        @RequestHeader(value = "X-Page-Url", required = false)
        String pageUrl) {

        AccessLogCreateCommand command = visitorCommandFactory.from(request, pageUrl);
        visitorService.saveAccessLog(command);

        if (!hasValidVisitorIdCookie(request)) {
            ResponseCookie cookie =
                ResponseCookie.from("YP-Visitor-Id", command.visitorId().toString())
                    .path("/")
                    .httpOnly(true)
                    .maxAge(365L * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.onSuccess());
        }

        return ResponseEntity.ok().body(ApiResponse.onSuccess());
    }

    private boolean hasValidVisitorIdCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if ("YP-Visitor-Id".equals(cookie.getName())) {
                return isValidUuid(cookie.getValue());
            }
        }
        return false;
    }

    private boolean isValidUuid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            java.util.UUID.fromString(value.trim());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Operation(summary = "방문자 통계 조회", description = "누적 방문자 수와 오늘 방문자 수를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": {
                        "totalVisitorCount": 20240,
                        "todayVisitorCount": 312
                      }
                    }
                    """)
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<VisitorResponse>> getVisitorCount() {
        VisitorResponse response = visitorService.getVisitorCount();
        return ResponseEntity.ok()
            .body(ApiResponse.onSuccess(response));
    }
}
