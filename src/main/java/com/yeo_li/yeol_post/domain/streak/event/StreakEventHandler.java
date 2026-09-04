package com.yeo_li.yeol_post.domain.streak.event;

import com.yeo_li.yeol_post.domain.post.event.PostPublishedEvent;
import com.yeo_li.yeol_post.domain.streak.service.StreakService;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreakEventHandler {

    private final StreakService streakService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostPublishedEvent event) {
        try {
            streakService.addStreakCount(event.publishedAt());
        } catch (Exception e) {
            log.error(StructuredLog.event(
                    "STREAK_UPDATE_FAILED",
                    "게시물 발행 후 스트릭 업데이트에 실패했습니다.",
                    "UNEXPECTED_ERROR"
                )
                .field("postId", event.postId())
                .field("publishedAt", event.publishedAt().toString())
                .throwable(e)
                .build());
        }
    }
}
