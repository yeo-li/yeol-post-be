package com.yeo_li.yeol_post.domain.subscription.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import org.springframework.http.HttpStatus;

public enum SubscriptionType implements BaseCode {
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "NOTIFICATION400", "이메일 형식이 유효하지 않습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION400", "이메일이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    SubscriptionType(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public Reason getReason() {
        return null;
    }

    @Override
    public Reason getReasonHttpStatus() {
        return null;
    }
}
