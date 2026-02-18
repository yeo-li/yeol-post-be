package com.yeo_li.yeol_post.domain.post.controller;


import com.yeo_li.yeol_post.domain.post.dto.PostCommandFactory;
import com.yeo_li.yeol_post.domain.post.dto.PostCreateRequest;
import com.yeo_li.yeol_post.domain.post.dto.PostResponse;
import com.yeo_li.yeol_post.domain.post.dto.PostUpdateRequest;
import com.yeo_li.yeol_post.domain.post.service.PostService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import com.yeo_li.yeol_post.global.common.swagger.ListPostResponseApiResponse;
import com.yeo_li.yeol_post.global.common.swagger.VoidApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    private final PostService postService;
    private final PostCommandFactory postCommandFactory;

    @Operation(summary = "게시물 저장", description = "사용자가 작성한 게시물을 저장합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "저장 성공"
            , content = @Content(schema = @Schema(implementation = VoidApiResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "게시물 생성 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "title": "Spring Security 권한 처리",
                  "summary": "hasRole과 authority 차이",
                  "author": "yeoli",
                  "content": "본문 내용",
                  "user_id": 1,
                  "category_id": 2,
                  "tags": [
                    "spring",
                    "security"
                  ]
                }
                """)
        )
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> savePost(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestBody @Valid PostCreateRequest request) {

        postService.createPost(postCommandFactory.createPostCommand(principal, request));

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
        @Parameter(description = "검색 파라미터(title, tag, category, author, limit, is_published)", example = "title=Spring Security")
        @RequestParam(required = false) Map<String, String> params) {
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
            "is_published")) { // 사용자(권한별)
            postResponses = postService.getPostRecent(Integer.parseInt(params.get("limit")),
                Boolean.parseBoolean(params.get("is_published")));
        } else if (params.containsKey("is_published")) { // 권한 사용자
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

    @Operation(summary = "게시물 삭제", description = "게시물 ID로 게시물을 삭제합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "삭제 성공"
            , content = @Content(schema = @Schema(implementation = VoidApiResponse.class)))
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
        @Parameter(description = "삭제할 게시물 ID", example = "10")
        @PathVariable("postId") Long postId
    ) {

        postService.deletePostByPostId(postId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "게시물 수정", description = "게시물 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "수정 성공"
            , content = @Content(schema = @Schema(implementation = VoidApiResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "게시물 수정 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "title": "Spring Security 권한 처리 (수정)",
                  "summary": "수정된 요약",
                  "author": "yeoli",
                  "content": "수정 본문",
                  "category_id": 2,
                  "tags": [
                    "spring",
                    "security",
                    "authorization"
                  ]
                }
                """)
        )
    )
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
        @Parameter(description = "수정할 게시물 ID", example = "10")
        @PathVariable("postId") Long postId,
        @RequestBody PostUpdateRequest request) {

        postService.updatePost(postId, request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }

    @Operation(summary = "게시물 조회수 증가", description = "게시물 조회수를 1 증가시킵니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "조회수 증가 성공"
            , content = @Content(schema = @Schema(implementation = VoidApiResponse.class)))
    })
    @PostMapping("/{postId}/views")
    public ResponseEntity<ApiResponse<Void>> increaseViews(
        @Parameter(description = "조회수 증가 대상 게시물 ID", example = "10")
        @PathVariable("postId") Long postId
    ) {
        postService.increaseViewCount(postId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess());
    }
}
