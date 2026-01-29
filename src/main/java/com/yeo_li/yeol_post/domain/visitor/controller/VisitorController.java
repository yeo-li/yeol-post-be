package com.yeo_li.yeol_post.domain.visitor.controller;

import com.yeo_li.yeol_post.domain.visitor.command.AccessLogCreateCommand;
import com.yeo_li.yeol_post.domain.visitor.dto.VisitorCommandFactory;
import com.yeo_li.yeol_post.domain.visitor.dto.response.VisitorResponse;
import com.yeo_li.yeol_post.domain.visitor.service.VisitorService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
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
public class VisitorController {

    private final VisitorService visitorService;
    private final VisitorCommandFactory visitorCommandFactory;


    @GetMapping("/access")
    public ResponseEntity<ApiResponse<Void>> accessVisitor(
        HttpServletRequest request,
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

    @GetMapping
    public ResponseEntity<ApiResponse<VisitorResponse>> getVisitorCount() {
        VisitorResponse response = visitorService.getVisitorCount();
        return ResponseEntity.ok()
            .body(ApiResponse.onSuccess(response));
    }
}
