package com.yeo_li.yeol_post.global.common.response.handler;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;

public class TagHandler extends GeneralException {

    public TagHandler(BaseCode errorCode) {
        super(errorCode);
    }
}
