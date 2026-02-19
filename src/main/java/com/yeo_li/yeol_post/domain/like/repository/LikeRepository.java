package com.yeo_li.yeol_post.domain.like.repository;

import com.yeo_li.yeol_post.domain.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsLikesByPostIdAndUserId(Long postId, Long userId);

    int countLikeByPostId(Long postId);

    void deleteLikeByPostIdAndUserId(Long postId, Long userId);
}
