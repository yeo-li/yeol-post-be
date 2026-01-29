package com.yeo_li.yeol_post.global.common.response.handler;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;

public class AdminHandler extends GeneralException {

    public AdminHandler(BaseCode errorCode) {
        super(errorCode);
    }
}
