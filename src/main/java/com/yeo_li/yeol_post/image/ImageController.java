package com.yeo_li.yeol_post.image;

import com.yeo_li.yeol_post.auth.AuthorizationService;
import com.yeo_li.yeol_post.common.response.ApiResponse;
import com.yeo_li.yeol_post.image.dto.ImageUploadResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
public class ImageController {

    private final AuthorizationService authorizationService;
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestParam("file") MultipartFile file
    ) {

        // 인증, 인가 검증
        Map<String, Object> attributes = principal.getAttributes();
        authorizationService.validateAdminAccess(String.valueOf(attributes.get("id")));

        ImageService.StoredImage storedImage = imageService.store(file);
        ImageUploadResponse response = new ImageUploadResponse(storedImage.url());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource resource = imageService.loadAsResource(filename);
        MediaType mediaType = imageService.resolveContentType(filename);
        return ResponseEntity.ok()
            .contentType(mediaType)
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
            .body(resource);
    }
}
