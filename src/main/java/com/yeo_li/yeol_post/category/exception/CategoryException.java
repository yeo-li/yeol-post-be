package com.yeo_li.yeol_post.category.exception;

import com.yeo_li.yeol_post.common.response.code.BaseCode;
import com.yeo_li.yeol_post.common.response.exception.GeneralException;

public class CategoryException extends GeneralException {

  public CategoryException(BaseCode errorCode) {
    super(errorCode);
  }
}
