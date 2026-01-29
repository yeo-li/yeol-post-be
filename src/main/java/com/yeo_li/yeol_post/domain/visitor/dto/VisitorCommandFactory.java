package com.yeo_li.yeol_post.domain.visitor.dto;

import com.yeo_li.yeol_post.domain.visitor.command.AccessLogCreateCommand;
import com.yeo_li.yeol_post.global.util.ClientIpExtractor;
import com.yeo_li.yeol_post.global.util.HashUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VisitorCommandFactory {

    @Value("${security.visitor.hmac-secret}")
    private String visitorHmacSecret;

    @Value("${security.visitor.trust-forwarded-header:false}")
    private boolean trustForwardedHeader;

    public AccessLogCreateCommand from(
        HttpServletRequest request,
        String pageUrl
    ) {

        String visitorIdCookie = findVisitorIdCookie(request);
        UUID visitorId = parseVisitorId(visitorIdCookie);

        String userAgent = request.getHeader("User-Agent");
        String ip = ClientIpExtractor.extract(request, trustForwardedHeader);
        String visitorHash = buildVisitorHash(ip, userAgent);

        String referer = request.getHeader("Referer");

        String osType = extractOsType(userAgent);
        String browserType = extractBrowserType(userAgent);

        return new AccessLogCreateCommand(
            visitorId,
            visitorHash,
            referer,
            osType,
            browserType,
            pageUrl
        );
    }

    private UUID parseVisitorId(String visitorIdHeader) {

        if (visitorIdHeader == null || visitorIdHeader.isBlank()) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(visitorIdHeader.trim());
        } catch (IllegalArgumentException ex) {
            return UUID.randomUUID();
        }
    }

    private String findVisitorIdCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (var cookie : request.getCookies()) {
            if ("YP-Visitor-Id".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String buildVisitorHash(String ip, String userAgent) {
        String source = (ip == null ? "" : ip) + "|" + (userAgent == null ? "" : userAgent);
        return HashUtils.hmacSha256(source, visitorHmacSecret);
    }

    private String extractOsType(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("mac os") || ua.contains("macintosh")) {
            return "Mac";
        }
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ios")) {
            return "iOS";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        return "Other";
    }

    private String extractBrowserType(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg")) {
            return "Edge";
        }
        if (ua.contains("opr") || ua.contains("opera")) {
            return "Opera";
        }
        if (ua.contains("chrome")) {
            return "Chrome";
        }
        if (ua.contains("safari")) {
            return "Safari";
        }
        if (ua.contains("firefox")) {
            return "Firefox";
        }
        return "Other";
    }
}
