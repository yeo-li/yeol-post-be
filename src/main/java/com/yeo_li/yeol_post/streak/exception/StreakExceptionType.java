package com.yeo_li.yeol_post.streak.exception;

import com.yeo_li.yeol_post.common.response.code.BaseCode;
import com.yeo_li.yeol_post.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StreakExceptionType implements BaseCode {
    WEEKLY_STREAK_NOT_FOUND(HttpStatus.NOT_FOUND, "WSTREAK404", "이번주 스트릭 정보가 존재하지 않습니다.");

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
