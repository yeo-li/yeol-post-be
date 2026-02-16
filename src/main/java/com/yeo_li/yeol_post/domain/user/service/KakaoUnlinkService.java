package com.yeo_li.yeol_post.domain.user.service;

import com.yeo_li.yeol_post.domain.user.exception.UserExceptionType;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class KakaoUnlinkService {

    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final RestClient restClient = RestClient.create();

    public void unlink(String kakaoAccessToken) {
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            throw new GeneralException(UserExceptionType.USER_OAUTH2_ACCESS_TOKEN_MISSING);
        }

        try {
            restClient.post()
                .uri(KAKAO_UNLINK_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            throw new GeneralException(UserExceptionType.USER_KAKAO_UNLINK_FAILED);
        }
    }
}
