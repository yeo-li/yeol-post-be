package com.yeo_li.yeol_post.domain.category;

import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import jakarta.persistence.CascadeType;
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
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "category_name")
    private String categoryName;

    @Column(name = "category_color")
    private String categoryColor;

    @Column(name = "category_description")
    private String categoryDescription;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}
