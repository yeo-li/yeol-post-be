package com.yeo_li.yeol_post.domain.category.exception;

import com.yeo_li.yeol_post.global.common.response.code.BaseCode;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;

public class CategoryException extends GeneralException {

    public CategoryException(BaseCode errorCode) {
        super(errorCode);
    }
}
