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

  // TODO: 임시 저장 기능 구현 -> 구현
  // TODO: 카테고리 추가 기능 구현
  // TODO: 설정 기능 구현
  // TODO: 소개 작성 기능 구현 -> 관리자 탭에서 구현
  // TODO: 페이지네이션 구현
  // TODO: 게시물 사진 첨구 기능 구현
  // TODO: 로그인 보안 강화
  // TODO: 관리자 전용 게시물 기능 구현

}
