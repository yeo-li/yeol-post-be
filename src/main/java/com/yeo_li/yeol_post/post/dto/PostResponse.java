package com.yeo_li.yeol_post.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yeo_li.yeol_post.category.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시물 응답 dto")
public record PostResponse(
    @Schema(description = "게시물 id", example = "10")
    @NotNull
    Long postId,

    @Schema(description = "게시물 제목", example = "Spring 완전 기초")
    @NotBlank
    String title,

    @Schema(description = "게시물 요약", example = "스프링 초보를 위한 글입니다.")
    String summary,

    @Schema(description = "저자", example = "서여")
    @NotBlank
    String author,

    @Schema(description = "게시물 본문", example = "Spring Boot와 JPA를 활용하면 복잡한 SQL 없이도 객체 중심의 도메인 설계를 할 수 있다. 본 글에서는 Entity, Repository, Service, Controller 계층으로 나누어 게시판을 어떻게 구성할 수 있는지에 대해 설명한다. 특히 JPA의 findById, save, delete 메서드를 중심으로 실습을 진행하며, 트랜잭션 관리와 Lazy Loading의 주의사항도 함께 다룬다.")
    @NotBlank
    String content,

    @Schema(description = "게시물 조회수", example = "34")
    @NotNull
    Long views,

    @NotNull
    @JsonProperty("is_published")
    @Schema(description = "게시 여부", example = "true")
    Boolean isPublished,

    @Schema(description = "발행일 및 시간", example = "2025-07-22 13:51:35.513454")
    @JsonFormat(timezone = "Asia/Seoul")
    @JsonProperty("published_at")
    LocalDateTime publishedAt,

    @Schema(description = "게시물 카테고리")
    CategoryResponse category,
    List<String> tags
) {

}
