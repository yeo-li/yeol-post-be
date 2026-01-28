package com.yeo_li.yeol_post.domain.image;

import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ImageService {

    private final Path uploadDir;

    public ImageService(@Value("${app.image.upload-dir:uploads/images}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 업로드 디렉터리를 생성할 수 없습니다.", e);
        }
    }

    public StoredImage store(MultipartFile file) {
        validateImage(file);

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String filename = generateUniqueFilename(extension);
        Path targetPath = uploadDir.resolve(filename).normalize();
        if (!targetPath.startsWith(uploadDir)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/images/")
            .path(filename)
            .toUriString();

        return new StoredImage(filename, url);
    }

    public Resource loadAsResource(String filename) {
        Path filePath = uploadDir.resolve(filename).normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
            }
            return resource;
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
        }
    }

    public MediaType resolveContentType(String filename) {
        Path filePath = uploadDir.resolve(filename).normalize();
        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType != null) {
                return MediaType.parseMediaType(contentType);
            }
        } catch (IOException ignored) {
            // fall through to default
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        String ext = filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        return ext.replaceAll("[^a-z0-9]", "");
    }

    private String generateUniqueFilename(String extension) {
        for (int attempt = 0; attempt < 5; attempt++) {
            String filename = UUID.randomUUID().toString().replace("-", "");
            if (!extension.isEmpty()) {
                filename += "." + extension;
            }
            Path targetPath = uploadDir.resolve(filename).normalize();
            if (!Files.exists(targetPath)) {
                return filename;
            }
        }
        throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
    }

    public record StoredImage(String filename, String url) {

    }
}
