package com.yeo_li.yeol_post.domain.feed.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import org.springframework.http.HttpStatus;

public enum FeedExceptionType implements BaseCode {
    FEED_CONTENT_INVALID(HttpStatus.BAD_REQUEST, "FEED400", "피드 내용이 유효하지 않습니다."),
    FEED_ACCESS_LEVEL_INVALID(HttpStatus.BAD_REQUEST, "FEED400", "피드 접근 권한이 유효하지 않습니다."),
    FEED_UPDATE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "FEED400", "수정할 피드 정보가 없습니다."),
    FEED_USER_ID_INVALID(HttpStatus.UNAUTHORIZED, "FEED401", "인증된 사용자 정보를 찾을 수 없습니다."),
    FEED_FORBIDDEN(HttpStatus.FORBIDDEN, "FEED403", "피드에 접근할 권한이 없습니다."),
    FEED_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "사용자를 찾을 수 없습니다."),
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "FEED404", "피드를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    FeedExceptionType(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

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
