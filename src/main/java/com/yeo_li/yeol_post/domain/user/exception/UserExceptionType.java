package com.yeo_li.yeol_post.domain.user.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserExceptionType implements BaseCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "사용자를 찾을 수 없습니다."),
    USER_OAUTH2_ID_MISSING(HttpStatus.UNAUTHORIZED, "USER401", "OAuth2 사용자 식별자(id)가 없습니다."),
    USER_ONBOARDING_INVALID(HttpStatus.BAD_REQUEST, "USER400", "닉네임, 이메일, 이메일 수신 여부를 모두 적어주세요."),
    USER_UPDATE_INVALID(HttpStatus.BAD_REQUEST, "USER400", "요청 값이 비어있습니다."),
    USER_EMAIL_INVALID(HttpStatus.BAD_REQUEST, "USER400", "이메일 형식이 올바르지 않습니다."),
    USER_NICKNAME_INVALID(HttpStatus.BAD_REQUEST, "USER400", "닉네임은 1자 이상 10자 이하여야 합니다."),
    USER_NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "USER400", "이미 사용 중인 닉네임입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public Reason getReason() {
        return Reason.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .data(null)
            .build();
    }

    @Override
    public Reason getReasonHttpStatus() {
        return Reason.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .data(null)
            .build();
    }
}
