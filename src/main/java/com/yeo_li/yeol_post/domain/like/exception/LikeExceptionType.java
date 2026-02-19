package com.yeo_li.yeol_post.domain.like.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikeExceptionType implements BaseCode {
    LIKE_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE404", "사용자를 찾을 수 없습니다."),
    LIKE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE404", "게시물을 찾을 수 없습니다."),
    LIKE_POST_ID_INVALID(HttpStatus.BAD_REQUEST, "LIKE400", "게시물 아이디가 올바르지 않습니다.");

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
