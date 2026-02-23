package com.yeo_li.yeol_post.domain.comment.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import org.springframework.http.HttpStatus;

public enum CommentExceptionType implements BaseCode {
    COMMENT_CONTENT_INVALID(HttpStatus.BAD_REQUEST, "COMMENT400", "댓글 내용이 유효하지 않습니다."),
    COMMENT_USER_ID_INVALID(HttpStatus.UNAUTHORIZED, "COMMENT401", "인증된 사용자 정보를 찾을 수 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMENT403", "댓글에 접근할 권한이 없습니다."),
    COMMENT_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "사용자를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "댓글을 찾을 수 없습니다."),
    COMMENT_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "게시물을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CommentExceptionType(HttpStatus httpStatus, String code, String message) {
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
