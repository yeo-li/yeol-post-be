package com.yeo_li.yeol_post.global.common.response.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private BaseCode errorCode;

    public GeneralException(BaseCode errorCode) {
        super(resolveMessage(errorCode));
        this.errorCode = errorCode;
    }

    public GeneralException(BaseCode errorCode, Throwable cause) {
        super(resolveMessage(errorCode), cause);
        this.errorCode = errorCode;
    }

    public Reason getErrorReason() {
        return this.errorCode.getReason();
    }

    public Reason getErrorReasonHttpStatus() {
        return this.errorCode.getReasonHttpStatus();
    }

    private static String resolveMessage(BaseCode errorCode) {
        if (errorCode == null) {
            return null;
        }

        Reason reasonHttpStatus = errorCode.getReasonHttpStatus();
        if (reasonHttpStatus != null) {
            return reasonHttpStatus.getMessage();
        }

        Reason reason = errorCode.getReason();
        if (reason != null) {
            return reason.getMessage();
        }
        return null;
    }
}
