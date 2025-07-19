package com.yeo_li.yeol_post.admin;

import com.yeo_li.yeol_post.common.entity.BaseTimeEntity;
import com.yeo_li.yeol_post.post.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String kakaoId;

  private String introduction;

  @NotNull
  private String name;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isDeleted;

  @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();
}