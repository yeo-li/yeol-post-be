package com.yeo_li.yeol_post.tag;

import com.yeo_li.yeol_post.common.response.ApiResponse;
import com.yeo_li.yeol_post.tag.dto.TagResponse;
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
public class TagController {

  private final TagService tagService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.onSuccess(tagService.getAllTags()));
  }
}
