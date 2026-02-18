package com.yeo_li.yeol_post.domain.post.controller;

import com.yeo_li.yeol_post.domain.post.dto.PostCommandFactory;
import com.yeo_li.yeol_post.domain.post.dto.PostCreateRequest;
import com.yeo_li.yeol_post.domain.post.dto.PostResponse;
import com.yeo_li.yeol_post.domain.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.domain.post.dto.response.DraftPostCreateResponse;
import com.yeo_li.yeol_post.domain.post.service.DraftPostService;
import com.yeo_li.yeol_post.domain.post.service.PostService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

    private final PostService postService;
    private final DraftPostService draftPostService;

    private final PostCommandFactory postCommandFactory;

    @Operation(summary = "임시 게시물 목록 조회", description = "임시 저장 상태의 게시물 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": []
                    }
                    """)
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getDrafts() {

        List<PostResponse> posts = postService.getAllDraftPosts();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(posts));
    }

    @Operation(summary = "임시 게시물 생성", description = "게시물을 임시 저장하고 게시물 ID를 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "생성 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": {
                        "post_id": 101
                      }
                    }
                    """)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "임시 게시물 생성 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "title": "OAuth2 로그인 구현",
                  "summary": "카카오 OAuth2 연결 과정",
                  "author": "yeoli",
                  "content": "본문 내용",
                  "user_id": 1,
                  "category_id": 2,
                  "tags": [
                    "oauth2",
                    "spring-security"
                  ]
                }
                """)
        )
    )
    @PostMapping
    public ResponseEntity<ApiResponse<DraftPostCreateResponse>> savePost(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody @Valid PostCreateRequest request) {

        Long postId = draftPostService.createDraftPost(
            postCommandFactory.createDraftPostCommand(principal, request));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(DraftPostCreateResponse.builder()
                .postId(postId)
                .build()
            ));
    }

    @Operation(summary = "임시 게시물 수정", description = "임시 저장 게시물의 내용을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "임시 게시물 수정 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "title": "OAuth2 로그인 구현 수정본",
                  "summary": "수정된 요약",
                  "author": "yeoli",
                  "content": "수정 본문",
                  "category_id": 2,
                  "tags": [
                    "oauth2",
                    "kakao"
                  ]
                }
                """)
        )
    )
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updateDraftPost(
        @Parameter(description = "수정할 임시 게시물 ID", example = "101")
        @PathVariable("postId") Long postId,
        @RequestBody PostUpdateRequest request) {

        draftPostService.updateDraftPost(postId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "임시 게시물 발행", description = "임시 저장 게시물을 발행 상태로 전환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "발행 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @PostMapping("/{postId}/publish")
    public ResponseEntity<ApiResponse<Void>> publishPost(
        @Parameter(description = "발행할 임시 게시물 ID", example = "101")
        @PathVariable Long postId) {

        draftPostService.publishPost(postId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

}
