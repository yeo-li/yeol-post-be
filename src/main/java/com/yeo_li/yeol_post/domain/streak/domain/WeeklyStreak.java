package com.yeo_li.yeol_post.domain.streak.domain;

import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주차별 작성 기록")
public class WeeklyStreak extends BaseTimeEntity {

    @Schema(description = "레코드 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "연도", example = "2026")
    @Column(nullable = false)
    private int year;

    @Schema(description = "주차 번호", example = "7")
    @Column(nullable = false)
    private int weekNumber;

    @Schema(description = "주차 시작 시각", example = "2026-02-09T00:00:00")
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Schema(description = "주차 종료 시각", example = "2026-02-15T23:59:59")
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Schema(description = "해당 주차의 게시물 수", example = "3")
    @Builder.Default
    @Column(nullable = false)
    private int postCount = 0;

    public void addPostCount() {
        this.postCount++;
    }

    public void removePostCount() {
        this.postCount--;
    }
}
