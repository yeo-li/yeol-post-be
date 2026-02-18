package com.yeo_li.yeol_post.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PostCreateRequest(
    @Schema(description = "게시물 제목", example = "SpringBoot 시작하기")
    @NotBlank
    String title,
    @Schema(description = "게시물 요악", example = "스프링부트가 처음이신 분들을 위한 글입니다.")
    String summary,
    @Schema(description = "저자", example = "서여")
    String author,
    @Schema(description = "게시물 본문", example = "Spring Boot와 JPA를 활용하면 복잡한 SQL 없이도 객체 중심의 도메인 설계를 할 수 있다. 본 글에서는 Entity, Repository, Service, Controller 계층으로 나누어 게시판을 어떻게 구성할 수 있는지에 대해 설명한다. 특히 JPA의 findById, save, delete 메서드를 중심으로 실습을 진행하며, 트랜잭션 관리와 Lazy Loading의 주의사항도 함께 다룬다.")
    @NotBlank
    String content,

    @Schema(description = "게시물에 지정된 카테고리 ID", example = "2")
    @JsonProperty("category_id")
    Long categoryId,
    @Schema(description = "게시물의 태그 목록", example = "[\"spring\", \"security\"]")
    List<String> tags) {

}
