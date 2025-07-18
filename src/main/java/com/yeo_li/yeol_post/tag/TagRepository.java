package com.yeo_li.yeol_post.tag;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Optional<Tag> findByTagName(String tagName);

  boolean existsByTagName(String tagName);
}

