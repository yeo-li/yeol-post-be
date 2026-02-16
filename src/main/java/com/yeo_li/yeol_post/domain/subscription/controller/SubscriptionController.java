package com.yeo_li.yeol_post.domain.subscription.controller;

import com.yeo_li.yeol_post.domain.subscription.dto.SubscriptionCountResponse;
import com.yeo_li.yeol_post.domain.subscription.dto.request.SubscriptionCreateRequest;
import com.yeo_li.yeol_post.domain.subscription.service.SubscriptionService;
import com.yeo_li.yeol_post.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "Subscription", description = "이메일 구독 API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "이메일 구독", description = "이메일 뉴스레터 구독을 활성화합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "구독 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "구독 요청 바디",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "email": "yeoli@example.com"
                }
                """)
        )
    )
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> subscribe(
        @RequestBody @Valid SubscriptionCreateRequest request
    ) {
        subscriptionService.subscribe(request.email());

        return ResponseEntity.ok().body(ApiResponse.onSuccess());
    }

    @Operation(summary = "이메일 구독 해지", description = "토큰을 이용해 이메일 구독을 해지합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "구독 해지 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "is_success": true,
                      "code": "GLOBAL200",
                      "message": "성공했습니다.",
                      "result": null
                    }
                    """)
            )
        )
    })
    @GetMapping("/unsubscribe/{token}")
    public ResponseEntity<ApiResponse<?>> unsubscribe(
        @Parameter(description = "구독 해지 토큰", example = "7e805b3a-6d7b-4f1a-8f0d-f8d89d7f9d21")
        @PathVariable @NotNull String token
    ) {

        subscriptionService.unsubscribe(token);

        return ResponseEntity.ok().body(ApiResponse.onSuccess());
    }

    @Operation(summary = "구독자 수 조회", description = "현재 구독자 수를 조회합니다.")
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
                      "result": {
                        "count": 128
                      }
                    }
                    """)
            )
        )
    })
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<SubscriptionCountResponse>> getSubscriptionsCount() {
        SubscriptionCountResponse response = subscriptionService.getSubscriptionCount();

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}
