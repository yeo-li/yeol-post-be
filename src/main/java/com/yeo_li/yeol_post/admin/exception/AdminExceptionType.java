package com.yeo_li.yeol_post.admin.exception;

import com.yeo_li.yeol_post.common.response.code.BaseCode;
import com.yeo_li.yeol_post.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminExceptionType implements BaseCode {
  ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "GOAL404", "목표를 찾을 수 없습니다."),
  ADMIN_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "GOAL400", "이미 완료된 목표입니다."),
  INVALID_ADMIN_DATE_RANGE(HttpStatus.BAD_REQUEST, "GOAL400", "유효하지 않은 목표 기간입니다."),
  ADMIN_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "GOAL400", "목표 제목은 필수입니다."),
  ADMIN_DURATION_INVALID(HttpStatus.BAD_REQUEST, "GOAL400", "목표 기간은 1일 이상이어야 합니다.");

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
