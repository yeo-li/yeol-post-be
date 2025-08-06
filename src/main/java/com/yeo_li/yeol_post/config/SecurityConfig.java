package com.yeo_li.yeol_post.config;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.frontend.origin}")
    private String frontendOrigin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors
                .configurationSource(corsFilter())
            )
            .csrf(csrf ->
                    csrf.disable()
                // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // XSRF-TOKEN 쿠키로 제공
//             .ignoringRequestMatchers("/logout")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/home", "/signup", "/login/**", "/oauth2/**",
                    "/logout",
                    "/error", "/api/v1/**")
                .permitAll()
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
                oauth2.successHandler((request, response, authentication) -> {
                    response.sendRedirect(frontendOrigin + "/login/success");
                })
            );

        return http.build();
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
            List.of("Authorization", "Cache-Control", "Content-Type", "X-XSRF-TOKEN"));
        // 클라이언트에서 접근 허용할 응답 헤더
        config.setExposedHeaders(List.of("X-XSRF-TOKEN"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 위 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}