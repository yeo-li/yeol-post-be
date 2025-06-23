package com.yeo_li.yeol_post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // 프론트 주소
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true); // ✅ 꼭 있어야 JSESSIONID 전달됨
      }
    };
  }
}
