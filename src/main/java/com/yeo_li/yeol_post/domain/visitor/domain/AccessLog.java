package com.yeo_li.yeol_post.domain.visitor.domain;

import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AccessLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID visitorId;

    @Nullable
    private String visitorHash;


    @Nullable
    private String referer;

    @Nullable
    private String osType;

    @Nullable
    private String browserType;


    @Nullable
    private String pageUrl;
}
