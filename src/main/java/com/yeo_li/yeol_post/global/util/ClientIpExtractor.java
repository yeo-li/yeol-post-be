package com.yeo_li.yeol_post.global.util;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpExtractor {

    public static String extract(HttpServletRequest request, boolean trustForwardedHeader) {
        if (trustForwardedHeader) {
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim(); // 첫 번째가 원본
            }

            String xri = request.getHeader("X-Real-IP");
            if (xri != null && !xri.isBlank()) {
                return xri;
            }
        }

        return request.getRemoteAddr();
    }
}
