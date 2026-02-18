package com.yeo_li.yeol_post.domain.streak.controller;

import com.yeo_li.yeol_post.domain.streak.dto.response.StreakResponse;
import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/streaks")
@RequiredArgsConstructor
@Tag(name = "Streak", description = "주간 스트릭 API")
public class WeeklyStreakController {

    private final StreakService streakService;

    @Operation(summary = "스트릭 조회", description = "현재 연속 작성 주차 수와 주차별 작성 기록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "currentStreakCount": 5,
                      "streaks": [
                        {
                          "id": 1,
                          "year": 2026,
                          "weekNumber": 7,
                          "startDateTime": "2026-02-09T00:00:00",
                          "endDateTime": "2026-02-15T23:59:59",
                          "postCount": 3
                        }
                      ]
                    }
                    """)
            )
        )
    })
    @GetMapping
    public ResponseEntity<StreakResponse> getStreaks() {
        StreakResponse response = streakService.getStreaks();
        return ResponseEntity.ok(response);
    }
}
