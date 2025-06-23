package com.yeo_li.yeol_post.config;

import com.yeo_li.yeol_post.admin.AdminOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AdminOAuth2UserService adminOAuth2UserService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**").permitAll()
            .anyRequest().authenticated()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/home")
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(adminOAuth2UserService)
            )
            .defaultSuccessUrl("/api/v1/admin/me", true)
        );
    return http.build();
  }
}
