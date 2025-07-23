package com.yeo_li.yeol_post.category.exception;

import com.yeo_li.yeol_post.common.response.code.BaseCode;
import com.yeo_li.yeol_post.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryExceptionType implements BaseCode {
  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404", "카테고리를 찾을 수 없습니다."),
  ADMIN_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "CATEGORY400", ""),
  INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY400", "유효하지 않은 카테고리입니다."),
  CATEGORY_NOT_DELETABLE(HttpStatus.CONFLICT, "CATEGORY409", "카테고리를 삭제할 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public Reason getReason() {
    return Reason.builder()
        .message(message)
        .code(code)
        .isSuccess(false)
        .data(null)
        .build();
  }

  @Override
  public Reason getReasonHttpStatus() {
    return Reason.builder()
        .message(message)
        .code(code)
        .isSuccess(false)
        .httpStatus(httpStatus)
        .data(null)
        .build();
  }
}
