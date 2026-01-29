package com.yeo_li.yeol_post.domain.tag;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTagName(String tagName);

    Tag findTagById(int tagId);
}

