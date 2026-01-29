package com.yeo_li.yeol_post.domain.streak.controller;

import com.yeo_li.yeol_post.domain.streak.dto.response.StreakResponse;
import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/streaks")
@RequiredArgsConstructor
public class WeeklyStreakController {

    private final StreakService streakService;

    @GetMapping
    public ResponseEntity<StreakResponse> getStreaks() {
        StreakResponse response = streakService.getStreaks();
        return ResponseEntity.ok(response);
    }
}
