package com.yeo_li.yeol_post.domain.post_tag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findPostTagsByPost_Id(Long post_id);

    List<PostTag> findPostTagsByTag_Id(Long tag_id);

    List<PostTag> findPostTagsByTag_IdOrderByCreatedAtDesc(Long tagId);

    @Query("""
        SELECT new com.yeo_li.yeol_post.domain.post_tag.PostTagName(
            pt.post.id,
            pt.tag.tagName
        )
        FROM PostTag pt
        WHERE pt.post.id IN :postIds
        """)
    List<PostTagName> findTagNamesByPostIds(@Param("postIds") List<Long> postIds);
}
