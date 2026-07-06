package com.yeo_li.yeol_post.domain.subscription.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class MailTemplateRenderer {

    public String render(String templatePath, Map<String, ?> variables) throws IOException {
        String rendered = StreamUtils.copyToString(
            new ClassPathResource(templatePath).getInputStream(),
            StandardCharsets.UTF_8
        );

        for (Map.Entry<String, ?> variable : variables.entrySet()) {
            String placeholder = "{" + variable.getKey() + "}";
            String value = variable.getValue() == null ? "" : variable.getValue().toString();
            rendered = rendered.replace(placeholder, value);
        }

        return rendered;
    }
}
