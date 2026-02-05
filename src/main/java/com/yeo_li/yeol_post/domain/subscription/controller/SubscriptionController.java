package com.yeo_li.yeol_post.domain.subscription.controller;

import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> subscribe(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        subscriptionService.subscribe(email);

        return ResponseEntity.ok().body(ApiResponse.onSuccess());
    }

    @GetMapping("/unsubscribe/{token}")
    public ResponseEntity<ApiResponse<?>> unsubscribe(@PathVariable @NotNull String token) {

        subscriptionService.unsubscribe(token);

        return ResponseEntity.ok().body(ApiResponse.onSuccess());
    }
}
