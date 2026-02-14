package com.yeo_li.yeol_post.domain.post.controller;

import com.yeo_li.yeol_post.domain.auth.AuthorizationService;
import com.yeo_li.yeol_post.domain.post.dto.PostCommandFactory;
import com.yeo_li.yeol_post.domain.post.dto.PostCreateRequest;
import com.yeo_li.yeol_post.domain.post.dto.PostResponse;
import com.yeo_li.yeol_post.domain.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.domain.post.dto.response.DraftPostCreateResponse;
import com.yeo_li.yeol_post.domain.post.service.DraftPostService;
import com.yeo_li.yeol_post.domain.post.service.PostService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drafts")
@RequiredArgsConstructor
@Tag(name = "Draft", description = "임시저장 게시물 관련 API")
public class DraftController {

    private final AuthorizationService authorizationService;
    private final PostService postService;
    private final DraftPostService draftPostService;

    private final PostCommandFactory postCommandFactory;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getDrafts(
        @AuthenticationPrincipal OAuth2User principal) {
        // 인가 사용자인지 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateUserAccess(String.valueOf(attributes.get("id")));

        List<PostResponse> posts = postService.getAllDraftPosts();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(posts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DraftPostCreateResponse>> savePost(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody @Valid PostCreateRequest request) {
        // 인가 사용자인지 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateUserAccess(String.valueOf(attributes.get("id")));

        Long postId = draftPostService.createDraftPost(
            postCommandFactory.createDraftPostCommand(request));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(DraftPostCreateResponse.builder()
                .postId(postId)
                .build()
            ));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updateDraftPost(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable("postId") Long postId,
        @RequestBody PostUpdateRequest request) {

        // 인가 사용자인지 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateUserAccess(String.valueOf(attributes.get("id")));

        draftPostService.updateDraftPost(postId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @PostMapping("/{postId}/publish")
    public ResponseEntity<ApiResponse<Void>> publishPost(
        @AuthenticationPrincipal OAuth2User principal,
        @PathVariable Long postId) {
        // 인가 사용자인지 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateUserAccess(String.valueOf(attributes.get("id")));

        draftPostService.publishPost(postId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

}
