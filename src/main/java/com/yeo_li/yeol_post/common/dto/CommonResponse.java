package com.yeo_li.yeol_post.common.dto;

import com.yeo_li.yeol_post.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

  private String code;
  private String message;
  private T data;

  public static <T> CommonResponse<T> success(T data) {
    return new CommonResponse<>("SUCCESS", "정상 처리되었습니다.", data);
  }

  public static CommonResponse<Void> fail(ErrorCode errorCode) {
    return new CommonResponse<>(errorCode.name(), errorCode.getMessage(), null);
  }
}