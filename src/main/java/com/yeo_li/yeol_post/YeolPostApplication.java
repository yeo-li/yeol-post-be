package com.yeo_li.yeol_post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class YeolPostApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeolPostApplication.class, args);
    }

}
