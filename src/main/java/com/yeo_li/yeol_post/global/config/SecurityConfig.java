package com.yeo_li.yeol_post.global.config;

import com.yeo_li.yeol_post.domain.user.service.UserOAuth2UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final int ADMIN_SESSION_TIMEOUT_SECONDS = 60 * 60 * 12; // 12h
    private static final int USER_SESSION_TIMEOUT_SECONDS = 60 * 60 * 24 * 30; // 30d

    @Value("${app.frontend.origin}")
    private String frontendOrigin;

    private final UserOAuth2UserService userOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieName("XSRF-TOKEN");
        csrfTokenRepository.setHeaderName("X-XSRF-TOKEN");
        csrfTokenRepository.setCookiePath("/");
        resolveCsrfCookieDomain().ifPresent(csrfTokenRepository::setCookieDomain);

        http
            .cors(cors -> cors
                .configurationSource(corsFilter())
            )
                .csrf(csrf -> csrf
                    .csrfTokenRepository(csrfTokenRepository)
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                    .ignoringRequestMatchers(
                        // 세션 인증과 무관한 공개 쓰기 API는 CSRF 예외 처리
                        PathPatternRequestMatcher.withDefaults()
                            .matcher(HttpMethod.POST, "/api/v1/subscriptions"),
                        PathPatternRequestMatcher.withDefaults()
                            .matcher(HttpMethod.POST, "/api/v1/subscriptions/"),
                        PathPatternRequestMatcher.withDefaults()
                            .matcher(HttpMethod.POST, "/api/v1/visitors/access"),
                        PathPatternRequestMatcher.withDefaults()
                            .matcher(HttpMethod.POST, "/api/v1/posts/*/views")
                    )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/home", "/signup", "/login/**", "/oauth2/**", "/logout",
                    "/error")
                .permitAll() // public page

                // public read endpoint
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/posts/**",
                    "/api/v1/categories",
                    "/api/v1/categories/recent",
                    "/api/v1/tags",
                    "/api/v1/images/**",
                    "/api/v1/streaks",
                    "/api/v1/visitors/**",
                    "/api/v1/subscriptions/**",
                    "/api/v1/users/me"
                ).permitAll()

                // public write endpoint
                .requestMatchers(HttpMethod.POST, "/api/v1/subscriptions/announcements")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/subscriptions",
                    "/api/v1/subscriptions/",
                    "/api/v1/visitors/access",
                    "/api/v1/posts/*/views"
                ).permitAll()

                // admin endpoint
                .requestMatchers("/api/v1/drafts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/posts",
                    "/api/v1/categories",
                    "/api/v1/images"
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,
                    "/api/v1/posts/**",
                    "/api/v1/categories/**",
                    "/api/v1/drafts/**"
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE,
                    "/api/v1/posts/**",
                    "/api/v1/categories/**"
                ).hasRole("ADMIN")
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/api/v1/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK); // redirect 없음
                })
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID", "XSRF-TOKEN") // 필요한 경우 쿠키 삭제
                .clearAuthentication(true)
                .permitAll()
            )
            .oauth2Login(oauth2 ->
                oauth2.userInfoEndpoint(userInfo -> userInfo.userService(userOAuth2UserService))
                    .successHandler((request, response, authentication) -> {
                        HttpSession session = request.getSession(false);
                        if (session != null && authentication != null) {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(
                                    authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
                            if (isAdmin) {
                                session.setMaxInactiveInterval(ADMIN_SESSION_TIMEOUT_SECONDS);
                            } else {
                                session.setMaxInactiveInterval(USER_SESSION_TIMEOUT_SECONDS);
                            }
                        }
                        response.sendRedirect(frontendOrigin + "/login/success");
                    })
            )
            .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

        return http.build();
    }

    private Optional<String> resolveCsrfCookieDomain() {
        try {
            String host = URI.create(frontendOrigin).getHost();
            if (host != null && host.endsWith(".yeo-li.com")) {
                return Optional.of("yeo-li.com");
            }
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Bean
    public CorsConfigurationSource corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 쿠키 포함 요청 허용
        config.setAllowCredentials(true);
        // 허용할 출처
        config.setAllowedOrigins(List.of(frontendOrigin));
        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 허용할 요청 헤더
        config.setAllowedHeaders(
            List.of(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-XSRF-TOKEN",
                "X-CSRF-TOKEN",
                "X-Page-Url",
                "X-Referer"
            ));
        // 클라이언트에서 접근 허용할 응답 헤더
        config.setExposedHeaders(List.of("X-XSRF-TOKEN"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 위 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static final class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken();
            }
            filterChain.doFilter(request, response);
        }
    }

    private static final class SpaCsrfTokenRequestHandler extends
        CsrfTokenRequestAttributeHandler {

        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
            Supplier<CsrfToken> csrfToken) {
            this.xor.handle(request, response, csrfToken);
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            String csrfHeaderValue = request.getHeader(csrfToken.getHeaderName());
            if (StringUtils.hasText(csrfHeaderValue)) {
                return this.plain.resolveCsrfTokenValue(request, csrfToken);
            }
            return this.xor.resolveCsrfTokenValue(request, csrfToken);
        }
    }
}
