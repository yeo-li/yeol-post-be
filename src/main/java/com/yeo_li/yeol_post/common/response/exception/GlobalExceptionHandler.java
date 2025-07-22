package com.yeo_li.yeol_post.common.response.exception;

import com.yeo_li.yeol_post.common.response.ApiResponse;
import com.yeo_li.yeol_post.common.response.code.Reason;
import com.yeo_li.yeol_post.common.response.code.resultCode.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
    String errorMessage = e.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

    return handleExceptionInternalConstraint(e,
        request);
  }

  @ExceptionHandler
  public ResponseEntity<Object> exception(Exception e, WebRequest request) {
    log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
    return handleExceptionInternalFalse(e, ErrorStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY,
        ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(), request, e.getMessage());
  }

  @ExceptionHandler(value = GeneralException.class)
  public ResponseEntity<Object> onThrowException(GeneralException generalException,
      HttpServletRequest request) {
    Reason errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
    return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e,
      WebRequest request) {
    log.warn("잘못된 인자가 전달됨: {}", e.getMessage());
    return handleExceptionInternalFalse(e, ErrorStatus.BAD_REQUEST, HttpHeaders.EMPTY,
        ErrorStatus.BAD_REQUEST.getHttpStatus(), request, e.getMessage());
  }

  @ExceptionHandler(value = IllegalStateException.class)
  public ResponseEntity<Object> handleIllegalStateException(IllegalStateException e,
      WebRequest request) {
    log.warn("잘못된 상태: {}", e.getMessage());
    return handleExceptionInternalFalse(e, ErrorStatus.BUSINESS_LOGIC_ERROR, HttpHeaders.EMPTY,
        ErrorStatus.BUSINESS_LOGIC_ERROR.getHttpStatus(), request, e.getMessage());
  }

  private ResponseEntity<Object> handleExceptionInternal(Exception e, Reason reason,
      HttpHeaders headers, HttpServletRequest request) {

    ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage());
//        e.printStackTrace();

    WebRequest webRequest = new ServletWebRequest(request);
    return super.handleExceptionInternal(
        e,
        body,
        headers,
        reason.getHttpStatus(),
        webRequest
    );
  }

  private ResponseEntity<Object> handleExceptionInternalFalse(Exception e,
      ErrorStatus errorCommonStatus,
      HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
    ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
        errorCommonStatus.getMessage(), errorPoint);
    return super.handleExceptionInternal(
        e,
        body,
        headers,
        status,
        request
    );
  }

  private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers,
      ErrorStatus errorCommonStatus,
      WebRequest request, Map<String, String> errorArgs) {
    ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
        errorCommonStatus.getMessage(), errorArgs);
    return super.handleExceptionInternal(
        e,
        body,
        headers,
        errorCommonStatus.getHttpStatus(),
        request
    );
  }

  private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e,
      WebRequest request) {
    ApiResponse<Object> body = ApiResponse.onFailure(ErrorStatus.VALIDATION_ERROR.getCode(),
        ErrorStatus.VALIDATION_ERROR.getMessage());
    return super.handleExceptionInternal(
        e,
        body,
        HttpHeaders.EMPTY,
        ErrorStatus.VALIDATION_ERROR.getHttpStatus(),
        request
    );
  }
}