package com.yeo_li.yeol_post.tag;

import com.yeo_li.yeol_post.common.entity.BaseTimeEntity;
import com.yeo_li.yeol_post.post_tag.PostTag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String tagName;

  @OneToMany(mappedBy = "tag")
  private List<PostTag> postTags = new ArrayList<>();

  public Tag(String tagName) {
    this.tagName = tagName;
  }

  public Tag() {
  }
}
