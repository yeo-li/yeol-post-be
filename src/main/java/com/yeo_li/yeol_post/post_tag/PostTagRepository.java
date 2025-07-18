package com.yeo_li.yeol_post.post_tag;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

  List<PostTag> findPostTagsByPost_Id(int post_id);

  List<PostTag> findPostTagsByTag_Id(int tag_id);
}
