package com.yeo_li.yeol_post.global.common.response.exception;

import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {

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

    // @Valid 예외 핸들링
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .findFirst()
            .orElse("잘못된 요청입니다.");

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.onFailure(ErrorStatus.VALIDATION_ERROR.getCode(), message));
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, Reason reason,
        HttpHeaders headers, HttpServletRequest request) {

        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage());
//        e.printStackTrace();

        // WebRequest webRequest = new ServletWebRequest(request);
        return new ResponseEntity<>(body, headers, reason.getHttpStatus());
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e,
        ErrorStatus errorCommonStatus,
        HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
            errorCommonStatus.getMessage(), errorPoint);
        return new ResponseEntity<>(body, headers, status);
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers,
        ErrorStatus errorCommonStatus,
        WebRequest request, Map<String, String> errorArgs) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
            errorCommonStatus.getMessage(), errorArgs);
        return new ResponseEntity<>(body, headers, errorCommonStatus.getHttpStatus());
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e,
        WebRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorStatus.VALIDATION_ERROR.getCode(),
            ErrorStatus.VALIDATION_ERROR.getMessage());
        return new ResponseEntity<>(body, HttpHeaders.EMPTY,
            ErrorStatus.VALIDATION_ERROR.getHttpStatus());
    }
}