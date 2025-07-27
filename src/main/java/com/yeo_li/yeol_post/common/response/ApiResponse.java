package com.yeo_li.yeol_post.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yeo_li.yeol_post.common.response.code.BaseCode;
import com.yeo_li.yeol_post.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.common.response.code.resultCode.SuccessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

  @Schema(description = "요청 성공 여부", example = "true")
  @JsonProperty("is_success")
  private final Boolean isSuccess;
  @Schema(description = "상태 코드", example = "GLOBAL200")
  private final String code;
  @Schema(description = "상태 메세지", example = "성공했습니다.")
  private final String message;
  //  @Schema(description = "response 데이터", example = "{\"tag_id\": 1, \"tag_name\": \"spring\"}")
  @Schema(description = "response 데이터")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T result;


  // 성공한 경우 응답 생성
  public static <T> ApiResponse<T> onSuccess(T result) {
    return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(),
        result);
  }

  public static <T> ApiResponse<T> of(BaseCode code, T result) {
    return new ApiResponse<>(true, code.getReasonHttpStatus().getCode(),
        code.getReasonHttpStatus().getMessage(), result);
  }

  // 성공 응답 (데이터 없음)
  public static <T> ApiResponse<T> onSuccess() {
    return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(),
        null);
  }

  // 실패한 경우 응답 생성
  public static <T> ApiResponse<T> onFailure(String code, String message) {
    return new ApiResponse<>(false, code, message, null);
  }

  public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
    return new ApiResponse<>(false, code, message, data);
  }

  // 실패한 경우 응답 생성
  public static <T> ApiResponse<T> onFailure(ErrorStatus errorStatus) {
    return new ApiResponse<>(false, errorStatus.getCode(), errorStatus.getMessage(), null);
  }

  public static <T> ApiResponse<T> onFailure(ErrorStatus errorStatus, T data) {
    return new ApiResponse<>(false, errorStatus.getCode(), errorStatus.getMessage(), data);
  }

}