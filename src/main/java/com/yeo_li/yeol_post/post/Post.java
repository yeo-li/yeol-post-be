package com.yeo_li.yeol_post.post;

import com.yeo_li.yeol_post.admin.Admin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String author;

  @Column(nullable = false)
  private Date updatedAt;

  private Date publishedAt;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isPublished;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isDeleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private Admin admin;

//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "category_id")
//  private Category category;
}
