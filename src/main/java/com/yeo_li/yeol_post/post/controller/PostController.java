package com.yeo_li.yeol_post.post.controller;

import com.yeo_li.yeol_post.auth.AuthorizationService;
import com.yeo_li.yeol_post.common.response.ApiResponse;
import com.yeo_li.yeol_post.common.swagger.ListPostResponseApiResponse;
import com.yeo_li.yeol_post.common.swagger.VoidApiResponse;
import com.yeo_li.yeol_post.post.dto.PostCommandFactory;
import com.yeo_li.yeol_post.post.dto.PostCreateRequest;
import com.yeo_li.yeol_post.post.dto.PostResponse;
import com.yeo_li.yeol_post.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시물 관련 API")
public class PostController {

  private final AuthorizationService authorizationService;
  private final PostService postService;
  private final PostCommandFactory postCommandFactory;

  @Operation(summary = "게시물 저장", description = "관리자가 작성한 게시물을 저장합니다.")
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "저장 성공"
          , content = @Content(schema = @Schema(implementation = VoidApiResponse.class)))
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> savePost(@AuthenticationPrincipal OAuth2User principal,
      @RequestBody @Valid PostCreateRequest request) {
    // 인가 사용자인지 검증
    Map<String, Object> attributes = principal.getAttributes();
    authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

    postService.createPost(postCommandFactory.createPostCommand(request));

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess());
  }

  @Operation(summary = "게시물 검색", description = "쿼리스트링으로 제목, 태그, 카테고리, 저자를 입력 받아 키워드와 관련된 게시물을 반환합니다.")
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "검색 성공"
          , content = @Content(schema = @Schema(implementation = ListPostResponseApiResponse.class)))
  })
  @GetMapping
  public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByQueryString(
      @RequestParam Map<String, String> params) {
    // TODO: sibal refactoring
    List<PostResponse> postResponses = new ArrayList<>();
    if (params.isEmpty()) {
      postResponses = postService.getAllPosts(); // 완료 모든 게시물 반환
    } else if (params.containsKey("title")) { // 사용자 -> 완료 출간된 게시물만 반환
      postResponses = postService.getPostByTitle(params.get("title"));
    } else if (params.containsKey("tag")) { // 사용자 -> 완료 -> 출간된 게시물만 반환
      postResponses = postService.getPostByTag(params.get("tag"));
    } else if (params.containsKey("category")) { // 사용자 -> 완료 -> 출간된 게시물만 반환
      postResponses = postService.getPostByCategory(params.get("category"));
    } else if (params.containsKey("author")) { // 사용자 -> 완료 -> 출간된 게시물만 반환
      postResponses = postService.getPostByAuthor(params.get("author"));
    } else if (params.containsKey("limit") && params.containsKey(
        "is_published")) { // 사용자 & 관리자 -> 여기만 검사해주면 될듯
      postResponses = postService.getPostRecent(Integer.parseInt(params.get("limit")),
          Boolean.parseBoolean(params.get("is_published")));
    } else if (params.containsKey("is_published")) { // 관리자
      if (Boolean.parseBoolean(params.get("is_published"))) {
        postResponses = postService.getAllPublishedPosts();
      } else {
        postResponses = postService.getAllDraftPosts();
      }
    } else if (params.containsKey("limit")) {
      postResponses = postService.getPostRecent(Integer.parseInt(params.get("limit")), true);
    }

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess(postResponses));

  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<?> deletePost(@PathVariable("postId") int postId) {

    postService.deletePostByPostId(postId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess());
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<?> updatePost(
      @AuthenticationPrincipal OAuth2User principal,
      @PathVariable("postId") Long postId,
      @RequestBody PostUpdateRequest request) {

    // 인가 사용자인지 검증
    Map<String, Object> attributes = principal.getAttributes();
    authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

    postService.updatePost(postId, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess());
  }
}