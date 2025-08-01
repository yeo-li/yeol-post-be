package com.yeo_li.yeol_post.post.domain;

import com.yeo_li.yeol_post.admin.domain.Admin;
import com.yeo_li.yeol_post.category.Category;
import com.yeo_li.yeol_post.common.entity.BaseTimeEntity;
import com.yeo_li.yeol_post.post_tag.PostTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Lob
  @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
  private String content;

  private String summary;

  @Column(nullable = false)
  private String author;

  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean isPublished;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean isDeleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private Admin admin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostTag> postTags = new ArrayList<>();
}
