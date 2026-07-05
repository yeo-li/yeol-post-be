package com.yeo_li.yeol_post.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MailTemplateRendererTest {

    private final MailTemplateRenderer mailTemplateRenderer = new MailTemplateRenderer();

    @Nested
    class RenderTest {

        @Test
        void render_변수가_주어지면_placeholder를_치환한다() throws IOException {
            String html = mailTemplateRenderer.render("mail/subscribed.html", Map.of(
                "frontendOrigin", "https://yeolpost.dev",
                "token", "verify-token-123"
            ));

            assertThat(html).contains("https://yeolpost.dev");
            assertThat(html).contains("verify-token-123");
            assertThat(html).doesNotContain("{frontendOrigin}");
            assertThat(html).doesNotContain("{token}");
        }

        @Test
        void render_변수값이_null이면_빈문자열로_치환한다() throws IOException {
            Map<String, Object> variables = new HashMap<>();
            variables.put("title", "제목");
            variables.put("summary", null);
            variables.put("postId", 1L);
            variables.put("frontendOrigin", "https://yeolpost.dev");
            variables.put("token", "verify-token-123");

            String html = mailTemplateRenderer.render("mail/post.html", variables);

            assertThat(html).contains("제목");
            assertThat(html).doesNotContain("{summary}");
        }

        @Test
        void render_답글알림템플릿의_placeholder를_치환한다() throws IOException {
            String html = mailTemplateRenderer.render("mail/reply-notification.html", Map.of(
                "frontendOrigin", "https://yeolpost.dev",
                "postId", 10L,
                "replyId", 201L,
                "postTitle", "게시물 제목",
                "replyAuthorNickname", "답글작성자",
                "replyContent", "답글 본문"
            ));

            assertThat(html).contains("게시물 제목");
            assertThat(html).contains("답글작성자");
            assertThat(html).contains("답글 본문");
            assertThat(html).contains("https://yeolpost.dev/posts/10#comment-201");
            assertThat(html).doesNotContain("{frontendOrigin}");
            assertThat(html).doesNotContain("{postId}");
            assertThat(html).doesNotContain("{replyId}");
            assertThat(html).doesNotContain("{postTitle}");
            assertThat(html).doesNotContain("{replyAuthorNickname}");
            assertThat(html).doesNotContain("{replyContent}");
        }

        @Test
        void render_게시물좋아요알림템플릿의_placeholder를_치환한다() throws IOException {
            String html = mailTemplateRenderer.render("mail/post-like-notification.html", Map.of(
                "frontendOrigin", "https://yeolpost.dev",
                "postId", 10L,
                "postTitle", "게시물 제목",
                "likerNickname", "좋아요작성자"
            ));

            assertThat(html).contains("게시물 제목");
            assertThat(html).contains("좋아요작성자");
            assertThat(html).contains("https://yeolpost.dev/posts/10");
            assertThat(html).doesNotContain("{frontendOrigin}");
            assertThat(html).doesNotContain("{postId}");
            assertThat(html).doesNotContain("{postTitle}");
            assertThat(html).doesNotContain("{likerNickname}");
        }
    }
}
