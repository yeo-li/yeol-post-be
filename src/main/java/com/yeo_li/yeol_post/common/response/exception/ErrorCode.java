package com.yeo_li.yeol_post.common.response.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // ======= 4xx =======
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근이 거부되었습니다."),
  DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "DATA_NOT_FOUND", "데이터를 찾을 수 없습니다."),

  // ======= 5xx =======
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
      "서버 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String code;     // 클라이언트 응답용 에러 코드
  private final String message;  // 사용자 메시지

  ErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}