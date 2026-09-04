package com.yeo_li.yeol_post.global.common.response.exception;

import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import com.yeo_li.yeol_post.global.common.response.code.Reason;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e,
        HttpServletRequest request) {
        log.warn(StructuredLog.event(
                "API_VALIDATION_FAILED",
                "입력값 검증에 실패했습니다.",
                ErrorStatus.VALIDATION_ERROR.getCode()
            )
            .field("method", request.getMethod())
            .field("path", request.getRequestURI())
            .field("status", ErrorStatus.VALIDATION_ERROR.getHttpStatus().value())
            .field("violationCount", e.getConstraintViolations().size())
            .build());

        return handleExceptionInternalConstraint(e, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, HttpServletRequest request) {
        log.error(StructuredLog.event(
                "API_UNEXPECTED_EXCEPTION",
                "API 요청 처리 중 예상하지 못한 예외가 발생했습니다.",
                ErrorStatus.INTERNAL_SERVER_ERROR.getCode()
            )
            .field("method", request.getMethod())
            .field("path", request.getRequestURI())
            .field("status", ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus().value())
            .throwable(e)
            .build());

        return handleExceptionInternalFalse(e, ErrorStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY,
            ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(), request, e.getMessage());
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> onThrowException(GeneralException generalException,
        HttpServletRequest request) {
        Reason errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        logGeneralException(generalException, errorReasonHttpStatus, request);
        return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e,
        HttpServletRequest request) {
        log.warn(StructuredLog.event(
                "API_ILLEGAL_ARGUMENT",
                "잘못된 요청 인자가 전달되었습니다.",
                ErrorStatus.BAD_REQUEST.getCode()
            )
            .field("method", request.getMethod())
            .field("path", request.getRequestURI())
            .field("status", ErrorStatus.BAD_REQUEST.getHttpStatus().value())
            .field("errorMessage", e.getMessage())
            .build());

        return handleExceptionInternalFalse(e, ErrorStatus.BAD_REQUEST, HttpHeaders.EMPTY,
            ErrorStatus.BAD_REQUEST.getHttpStatus(), request, e.getMessage());
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException e,
        HttpServletRequest request) {
        log.warn(StructuredLog.event(
                "API_ILLEGAL_STATE",
                "요청한 작업을 처리할 수 없는 상태입니다.",
                ErrorStatus.BUSINESS_LOGIC_ERROR.getCode()
            )
            .field("method", request.getMethod())
            .field("path", request.getRequestURI())
            .field("status", ErrorStatus.BUSINESS_LOGIC_ERROR.getHttpStatus().value())
            .field("errorMessage", e.getMessage())
            .build());

        return handleExceptionInternalFalse(e, ErrorStatus.BUSINESS_LOGIC_ERROR, HttpHeaders.EMPTY,
            ErrorStatus.BUSINESS_LOGIC_ERROR.getHttpStatus(), request, e.getMessage());
    }

    // @Valid 예외 핸들링
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e, HttpServletRequest request) {

        String message = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .findFirst()
            .orElse("잘못된 요청입니다.");

        String field = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getField)
            .findFirst()
            .orElse(null);

        log.warn(StructuredLog.event(
                "API_REQUEST_BODY_VALIDATION_FAILED",
                "요청 본문 검증에 실패했습니다.",
                ErrorStatus.VALIDATION_ERROR.getCode()
            )
            .field("method", request.getMethod())
            .field("path", request.getRequestURI())
            .field("status", HttpStatus.BAD_REQUEST.value())
            .field("field", field)
            .field("validationMessage", message)
            .build());

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
        HttpHeaders headers, HttpStatus status, HttpServletRequest request, String errorPoint) {
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
        HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorStatus.VALIDATION_ERROR.getCode(),
            ErrorStatus.VALIDATION_ERROR.getMessage());
        return new ResponseEntity<>(body, HttpHeaders.EMPTY,
            ErrorStatus.VALIDATION_ERROR.getHttpStatus());
    }

    private void logGeneralException(GeneralException exception, Reason reason,
        HttpServletRequest request) {
        HttpStatus status = reason.getHttpStatus();
        int statusCode = status == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : status.value();

        if (statusCode >= 500) {
            log.error(StructuredLog.event(
                    "API_EXPECTED_EXCEPTION_FAILED",
                    reason.getMessage(),
                    reason.getCode()
                )
                .field("method", request.getMethod())
                .field("path", request.getRequestURI())
                .field("status", statusCode)
                .field("errorCode", reason.getCode())
                .throwable(exception)
                .build());
            return;
        }

        log.warn(StructuredLog.event(
                "API_EXPECTED_EXCEPTION",
                reason.getMessage(),
                reason.getCode()
            )
            .field("method", request.getMethod())
            .field("path", request.getRequestURI())
            .field("status", statusCode)
            .field("errorCode", reason.getCode())
            .field("exceptionType", exception.getClass().getName())
            .build());
    }
}
