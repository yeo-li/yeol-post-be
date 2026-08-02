package com.yeo_li.yeol_post.domain.feed.repository;

import com.yeo_li.yeol_post.domain.feed.entity.FeedLike;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    @Modifying
    @Query(value = """
        INSERT IGNORE INTO feed_like (user_id, feed_id, created_at, updated_at)
        VALUES (:userId, :feedId, NOW(6), NOW(6))
        """, nativeQuery = true)
    int insertIgnore(
        @Param("userId") Long userId,
        @Param("feedId") Long feedId
    );

    void deleteByFeedIdAndUserId(Long feedId, Long userId);

    @Query("""
        SELECT new com.yeo_li.yeol_post.domain.feed.repository.FeedLikeCount(
            fl.feed.id,
            COUNT(fl.id)
        )
        FROM FeedLike fl
        WHERE fl.feed.id IN :feedIds
        GROUP BY fl.feed.id
        """)
    List<FeedLikeCount> countByFeedIds(@Param("feedIds") Collection<Long> feedIds);

    @Query("""
        SELECT fl.feed.id
        FROM FeedLike fl
        WHERE fl.user.id = :userId
          AND fl.feed.id IN :feedIds
        """)
    List<Long> findLikedFeedIds(
        @Param("userId") Long userId,
        @Param("feedIds") Collection<Long> feedIds
    );
}
