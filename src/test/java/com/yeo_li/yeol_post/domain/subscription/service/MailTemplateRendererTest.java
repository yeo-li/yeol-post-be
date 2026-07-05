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
    }
}
