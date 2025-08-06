package com.yeo_li.yeol_post.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.frontend.origin}")
    private String frontendOrigin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf ->
                    csrf.disable()
                // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // XSRF-TOKEN 쿠키로 제공
//             .ignoringRequestMatchers("/logout")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/home", "/signup", "/login/**", "/oauth2/**", "/logout",
                    "/error", "/api/v1/**")
                .permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
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
}