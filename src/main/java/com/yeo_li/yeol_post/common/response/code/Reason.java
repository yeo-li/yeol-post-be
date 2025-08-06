package com.yeo_li.yeol_post.common.response.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class Reason {

  private HttpStatus httpStatus;

  private final boolean isSuccess;
  private final String code;
  private final String message;
  private final Object data;

}
