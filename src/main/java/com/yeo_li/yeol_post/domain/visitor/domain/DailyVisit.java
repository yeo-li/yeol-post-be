package com.yeo_li.yeol_post.domain.visitor.domain;

import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "daily_visit",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"visit_date"})
    }
)
public class DailyVisit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "visit_date")
    private LocalDate visitDate = LocalDate.now();

    @NotNull
    private Long visitCount = 0L;
}
