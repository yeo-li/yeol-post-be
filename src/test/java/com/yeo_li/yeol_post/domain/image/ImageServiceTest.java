package com.yeo_li.yeol_post.domain.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class ImageServiceTest {

    private static final byte[] PNG_SIGNATURE =
        new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    @TempDir
    Path uploadDir;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void store_PNG_이미지를_검증하고_재인코딩해_저장한다() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ImageService imageService = new ImageService(uploadDir.toString(), DataSize.ofMegabytes(1));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "sample.png",
            "image/png",
            pngBytes()
        );

        ImageService.StoredImage storedImage = imageService.store(file);

        Path savedFile = uploadDir.resolve(storedImage.filename());
        assertThat(storedImage.filename()).endsWith(".png");
        assertThat(Files.exists(savedFile)).isTrue();
        assertThat(Files.readAllBytes(savedFile))
            .startsWith(PNG_SIGNATURE);
    }

    @Test
    void store_SVG_이미지는_차단한다() {
        ImageService imageService = new ImageService(uploadDir.toString(), DataSize.ofMegabytes(1));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "malicious.svg",
            "image/svg+xml",
            "<svg><script>alert(1)</script></svg>".getBytes()
        );

        assertThatThrownBy(() -> imageService.store(file))
            .isInstanceOfSatisfying(GeneralException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.UNSUPPORTED_MEDIA_TYPE)
            );
    }

    @Test
    void store_이미지_MIME으로_위장한_비이미지는_차단한다() {
        ImageService imageService = new ImageService(uploadDir.toString(), DataSize.ofMegabytes(1));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "fake.png",
            "image/png",
            "not an image".getBytes()
        );

        assertThatThrownBy(() -> imageService.store(file))
            .isInstanceOfSatisfying(GeneralException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.UNSUPPORTED_MEDIA_TYPE)
            );
    }

    @Test
    void store_MIME과_확장자가_일치하지_않으면_차단한다() throws IOException {
        ImageService imageService = new ImageService(uploadDir.toString(), DataSize.ofMegabytes(1));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "sample.jpg",
            "image/png",
            pngBytes()
        );

        assertThatThrownBy(() -> imageService.store(file))
            .isInstanceOfSatisfying(GeneralException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.UNSUPPORTED_MEDIA_TYPE)
            );
    }

    @Test
    void store_허용_용량을_초과하면_차단한다() throws IOException {
        ImageService imageService = new ImageService(uploadDir.toString(), DataSize.ofBytes(10));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "sample.png",
            "image/png",
            pngBytes()
        );

        assertThatThrownBy(() -> imageService.store(file))
            .isInstanceOfSatisfying(GeneralException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.PAYLOAD_TOO_LARGE)
            );
    }

    @Test
    void store_관리자면_허용_용량을_초과해도_저장한다() throws IOException {
        setupRequestContext();
        ImageService imageService = new ImageService(uploadDir.toString(), DataSize.ofBytes(10));
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "sample.png",
            "image/png",
            pngBytes()
        );

        ImageService.StoredImage storedImage = imageService.store(file, true);

        Path savedFile = uploadDir.resolve(storedImage.filename());
        assertThat(Files.exists(savedFile)).isTrue();
        assertThat(Files.readAllBytes(savedFile))
            .startsWith(PNG_SIGNATURE);
    }

    private void setupRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private byte[] pngBytes() throws IOException {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(1, 0, Color.GREEN.getRGB());
        image.setRGB(0, 1, Color.BLUE.getRGB());
        image.setRGB(1, 1, Color.WHITE.getRGB());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}
