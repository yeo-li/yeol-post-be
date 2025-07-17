package com.yeo_li.yeol_post.common.exception;

import com.yeo_li.yeol_post.common.dto.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> handleBusinessException(BusinessException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(CommonResponse.fail(e.getErrorCode()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleOtherExceptions(Exception e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}