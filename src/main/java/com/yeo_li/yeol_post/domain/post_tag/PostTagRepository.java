package com.yeo_li.yeol_post.domain.post_tag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findPostTagsByPost_Id(Long post_id);

    List<PostTag> findPostTagsByTag_Id(Long tag_id);

    List<PostTag> findPostTagsByTag_IdOrderByCreatedAtDesc(Long tagId);
}
