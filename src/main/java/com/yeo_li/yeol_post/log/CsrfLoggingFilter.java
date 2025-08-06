package com.yeo_li.yeol_post.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CsrfLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String method = request.getMethod();
        String csrfHeader = request.getHeader("X-XSRF-TOKEN");
        Cookie[] cookies = request.getCookies();
        String csrfCookie = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("XSRF-TOKEN".equals(cookie.getName())) {
                    csrfCookie = cookie.getValue();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}