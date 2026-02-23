package com.yeo_li.yeol_post.global.util;

import java.util.Map;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Utils {

    public static String getKakaoId(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes.get("id") == null) {
            return null;
        }
        return String.valueOf(attributes.get("id"));
    }
}
