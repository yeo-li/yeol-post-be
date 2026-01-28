package com.yeo_li.yeol_post.domain.post_tag;

import com.yeo_li.yeol_post.global.common.entity.BaseTimeEntity;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.tag.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post_tag")
public class PostTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }

    public PostTag() {

    }
}
