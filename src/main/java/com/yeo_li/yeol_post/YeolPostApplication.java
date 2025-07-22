package com.yeo_li.yeol_post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YeolPostApplication {

  public static void main(String[] args) {
    SpringApplication.run(YeolPostApplication.class, args);
  }

}
