package com.yeo_li.yeol_post.common.response.code.resultCode;

import com.yeo_li.yeol_post.common.response.code.BaseCode;
import com.yeo_li.yeol_post.common.response.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

  _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
  _CREATED(HttpStatus.CREATED, "COMMON201", "생성되었습니다.");


  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public Reason getReason() {
    return Reason.builder()
        .message(message)
        .code(code)
        .isSuccess(true)
        .data(null)
        .build();
  }

  @Override
  public Reason getReasonHttpStatus() {
    return Reason.builder()
        .message(message)
        .code(code)
        .isSuccess(true)
        .httpStatus(httpStatus)
        .data(null)
        .build();
  }
}
