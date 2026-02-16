package com.yeo_li.yeol_post.domain.image;

import com.yeo_li.yeol_post.domain.image.dto.ImageUploadResponse;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 업로드/조회 API")
public class ImageController {
    
    private final ImageService imageService;

    @Operation(summary = "이미지 업로드", description = "이미지 파일을 업로드하고 접근 URL을 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업로드 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": {
                        "url": "https://api.example.com/api/v1/images/3f5b2a.png"
                      }
                    }
                    """)
            )
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
        @Parameter(
            description = "업로드할 이미지 파일",
            required = true,
            content = @Content(mediaType = "multipart/form-data",
                schema = @Schema(type = "string", format = "binary"))
        )
        @RequestParam("file") MultipartFile file
    ) {

        ImageService.StoredImage storedImage = imageService.store(file);
        ImageUploadResponse response = new ImageUploadResponse(storedImage.url());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "이미지 조회", description = "파일명으로 업로드 이미지를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(mediaType = "image/*",
                schema = @Schema(type = "string", format = "binary"))
        )
    })
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(
        @Parameter(description = "조회할 파일명", example = "3f5b2a.png")
        @PathVariable String filename
    ) {
        Resource resource = imageService.loadAsResource(filename);
        MediaType mediaType = imageService.resolveContentType(filename);
        return ResponseEntity.ok()
            .contentType(mediaType)
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
            .body(resource);
    }
}
