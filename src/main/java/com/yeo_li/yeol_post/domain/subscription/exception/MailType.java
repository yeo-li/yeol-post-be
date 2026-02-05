package com.yeo_li.yeol_post.domain.subscription.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;

public enum MailType implements BaseCode {
    MAIL_SEND_FAILED("MAIL500", "이메일 전송에 실패했습니다.");

    private final String code;
    private final String message;

    MailType(String code, String message) {
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
