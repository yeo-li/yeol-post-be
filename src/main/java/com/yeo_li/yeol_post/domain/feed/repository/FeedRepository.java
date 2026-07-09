package com.yeo_li.yeol_post.domain.feed.repository;

import com.yeo_li.yeol_post.domain.feed.entity.Feed;
import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @EntityGraph(attributePaths = "author")
    @Query("""
        SELECT f
        FROM Feed f
        WHERE f.requiredAccessLevel IN :contentAccessLevel
          AND f.deletedAt IS NULL
        ORDER BY f.createdAt DESC
        """)
    List<Feed> findAccessibleFeeds(
        @NotNull @Param("contentAccessLevel") List<ContentAccessLevel> contentAccessLevel);

    @EntityGraph(attributePaths = "author")
    Optional<Feed> findByIdAndDeletedAtIsNull(Long id);
}
