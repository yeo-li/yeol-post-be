package com.yeo_li.yeol_post.domain.image;

import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ImageService {

    private static final byte[] PNG_SIGNATURE =
        new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final String SVG_MEDIA_TYPE = "image/svg+xml";
    private static final Map<String, ImageFormat> IMAGE_FORMAT_BY_MEDIA_TYPE = Map.of(
        MediaType.IMAGE_JPEG_VALUE, ImageFormat.JPEG,
        "image/jpg", ImageFormat.JPEG,
        MediaType.IMAGE_PNG_VALUE, ImageFormat.PNG
    );

    private final Path uploadDir;
    private final long maxFileSizeBytes;

    public ImageService(
        @Value("${app.image.upload-dir:uploads/images}") String uploadDir,
        @Value("${app.image.max-file-size:5MB}") DataSize maxFileSize
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSizeBytes = maxFileSize.toBytes();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 업로드 디렉터리를 생성할 수 없습니다.", e);
        }
    }

    public StoredImage store(MultipartFile file) {
        byte[] bytes = readAndValidateFileBytes(file);
        ImageFormat imageFormat = validateImage(file, bytes);
        BufferedImage image = decodeImage(bytes);

        String filename = generateUniqueFilename(imageFormat.extension);
        Path targetPath = uploadDir.resolve(filename).normalize();
        if (!targetPath.startsWith(uploadDir)) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        try {
            reencodeImage(image, imageFormat, targetPath);
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
        validateServableImageFilename(filename);

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
        return resolveFormatFromFilename(filename).mediaType;
    }

    private byte[] readAndValidateFileBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new GeneralException(ErrorStatus.PAYLOAD_TOO_LARGE);
        }

        try {
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new GeneralException(ErrorStatus.BAD_REQUEST);
            }
            if (bytes.length > maxFileSizeBytes) {
                throw new GeneralException(ErrorStatus.PAYLOAD_TOO_LARGE);
            }
            return bytes;
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
    }

    private ImageFormat validateImage(MultipartFile file, byte[] bytes) {
        String declaredContentType = normalizeContentType(file.getContentType());
        String extension = extractExtension(file.getOriginalFilename());

        if (isSvg(declaredContentType, extension, bytes)) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        ImageFormat declaredFormat = IMAGE_FORMAT_BY_MEDIA_TYPE.get(declaredContentType);
        if (declaredFormat == null) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        if (!extension.isBlank() && !declaredFormat.matchesExtension(extension)) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        ImageFormat magicFormat = detectMagicBytes(bytes);
        if (magicFormat == null || magicFormat != declaredFormat) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        return magicFormat;
    }

    private BufferedImage decodeImage(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
                throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            return image;
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    private void reencodeImage(BufferedImage image, ImageFormat imageFormat, Path targetPath)
        throws IOException {
        BufferedImage outputImage = imageFormat == ImageFormat.JPEG
            ? convertToRgb(image)
            : image;

        try (OutputStream outputStream = Files.newOutputStream(targetPath)) {
            boolean written = ImageIO.write(outputImage, imageFormat.writerFormat, outputStream);
            if (!written) {
                throw new IOException("No ImageIO writer found for " + imageFormat.writerFormat);
            }
        }
    }

    private BufferedImage convertToRgb(BufferedImage image) {
        BufferedImage rgbImage =
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgbImage.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.drawImage(image, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return rgbImage;
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null) {
            return "";
        }
        int separatorIndex = contentType.indexOf(';');
        String normalized = separatorIndex < 0 ? contentType : contentType.substring(0, separatorIndex);
        return normalized.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isSvg(String declaredContentType, String extension, byte[] bytes) {
        if (SVG_MEDIA_TYPE.equals(declaredContentType) || "svg".equals(extension)) {
            return true;
        }

        String prefix = new String(bytes, 0, Math.min(bytes.length, 512), StandardCharsets.UTF_8)
            .trim()
            .toLowerCase(Locale.ROOT);
        return prefix.startsWith("<svg") || (prefix.startsWith("<?xml") && prefix.contains("<svg"));
    }

    private ImageFormat detectMagicBytes(byte[] bytes) {
        if (bytes.length >= 3
            && (bytes[0] & 0xFF) == 0xFF
            && (bytes[1] & 0xFF) == 0xD8
            && (bytes[2] & 0xFF) == 0xFF) {
            return ImageFormat.JPEG;
        }

        if (bytes.length >= PNG_SIGNATURE.length
            && Arrays.equals(Arrays.copyOf(bytes, PNG_SIGNATURE.length), PNG_SIGNATURE)) {
            return ImageFormat.PNG;
        }

        return null;
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

    private void validateServableImageFilename(String filename) {
        resolveFormatFromFilename(filename);
    }

    private ImageFormat resolveFormatFromFilename(String filename) {
        String extension = extractExtension(filename);
        for (ImageFormat imageFormat : ImageFormat.values()) {
            if (imageFormat.matchesExtension(extension)) {
                return imageFormat;
            }
        }
        throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
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

    private enum ImageFormat {
        JPEG("jpg", "jpeg", MediaType.IMAGE_JPEG, "jpg", "jpeg"),
        PNG("png", "png", MediaType.IMAGE_PNG, "png");

        private final String extension;
        private final String writerFormat;
        private final MediaType mediaType;
        private final String[] acceptedExtensions;

        ImageFormat(String extension, String writerFormat, MediaType mediaType,
            String... acceptedExtensions) {
            this.extension = extension;
            this.writerFormat = writerFormat;
            this.mediaType = mediaType;
            this.acceptedExtensions = acceptedExtensions;
        }

        private boolean matchesExtension(String extension) {
            for (String acceptedExtension : acceptedExtensions) {
                if (acceptedExtension.equals(extension)) {
                    return true;
                }
            }
            return false;
        }
    }
}
