package com.yeo_li.yeol_post.domain.tag;

import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import com.yeo_li.yeol_post.domain.tag.dto.TagResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "Tag", description = "태그 조회 API")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "태그 목록 조회", description = "등록된 전체 태그를 조회합니다.")
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
                      "result": [
                        {
                          "tag_id": 1,
                          "tag_name": "spring"
                        }
                      ]
                    }
                    """)
            )
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(tagService.getAllTags()));
    }
}
