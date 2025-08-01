package com.yeo_li.yeol_post.post.controller;

import com.yeo_li.yeol_post.auth.AuthorizationService;
import com.yeo_li.yeol_post.common.response.ApiResponse;
import com.yeo_li.yeol_post.post.dto.PostResponse;
import com.yeo_li.yeol_post.post.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drafts")
@RequiredArgsConstructor
@Tag(name = "Draft", description = "임시저장 게시물 관련 API")
public class DraftController {

  private final AuthorizationService authorizationService;
  private final PostService postService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<PostResponse>>> getDrafts(
      @AuthenticationPrincipal OAuth2User principal) {
    // 인가 사용자인지 검증
    Map<String, Object> attributes = principal.getAttributes();
    authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

    List<PostResponse> posts = postService.getAllDraftPosts();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess(posts));
  }


}
