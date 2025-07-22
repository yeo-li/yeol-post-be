package com.yeo_li.yeol_post.post;

import com.yeo_li.yeol_post.auth.AuthorizationService;
import com.yeo_li.yeol_post.common.response.ApiResponse;
import com.yeo_li.yeol_post.post.dto.PostCreateRequest;
import com.yeo_li.yeol_post.post.dto.PostResponse;
import com.yeo_li.yeol_post.post.dto.PostUpdateRequest;
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
public class PostController {

  private final AuthorizationService authorizationService;
  private final PostService postService;
  private final PostCommandFactory postCommandFactory;

  @PostMapping
  public ResponseEntity<?> savePost(@AuthenticationPrincipal OAuth2User principal,
      @RequestBody @Valid PostCreateRequest request) {
    System.out.println(request.toString());

    // 인가 사용자인지 검증
    Map<String, Object> attributes = principal.getAttributes();
    authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

    postService.createPost(postCommandFactory.createPostCommand(request));

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess());
  }

  @GetMapping
  public ResponseEntity<?> getPostsByQueryString(@RequestParam Map<String, String> params) {
    List<PostResponse> postResponses = new ArrayList<>();
    if (params.containsKey("title")) {
      postResponses = postService.getPostByTitle(params.get("title"));
    } else if (params.containsKey("tag")) {
      postResponses = postService.getPostByTag(params.get("tag"));
    } else if (params.containsKey("category")) {
      postResponses = postService.getPostByCategory(params.get("category"));
    } else if (params.containsKey("author")) {
      postResponses = postService.getPostByAuthor(params.get("author"));
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
      @RequestBody PostUpdateRequest request) {

    // 인가 사용자인지 검증
//    Map<String, Object> attributes = principal.getAttributes();
//    authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

    postService.updatePost(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess());
  }
}