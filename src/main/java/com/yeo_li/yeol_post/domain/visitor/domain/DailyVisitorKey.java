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
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "daily_visitor_key",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"visit_date", "visitor_id"}
        )
    }
)
public class DailyVisitorKey extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate visitDate = LocalDate.now();

    @NotNull
    @Column(nullable = false, length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID visitorId;
}
