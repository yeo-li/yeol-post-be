package com.yeo_li.yeol_post.domain.post.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostExceptionType implements BaseCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST404", "게시물을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public Reason getReason() {
        return null;
    }

    @Override
    public Reason getReasonHttpStatus() {
        return null;
    }
}
