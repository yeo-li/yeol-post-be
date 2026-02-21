package com.yeo_li.yeol_post.domain.comment.repository;

import com.yeo_li.yeol_post.domain.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByPostIdAndParentCommentIsNull(Long postId);

    List<Comment> findCommentsByParentComment(Comment parentComment);
}
