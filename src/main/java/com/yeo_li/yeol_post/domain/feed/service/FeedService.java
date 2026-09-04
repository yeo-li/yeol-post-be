package com.yeo_li.yeol_post.domain.feed.service;

import com.yeo_li.yeol_post.domain.feed.dto.request.FeedCreateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.request.FeedUpdateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.response.FeedResponse;
import com.yeo_li.yeol_post.domain.feed.entity.Feed;
import com.yeo_li.yeol_post.domain.feed.exception.FeedExceptionType;
import com.yeo_li.yeol_post.domain.feed.repository.FeedLikeCount;
import com.yeo_li.yeol_post.domain.feed.repository.FeedLikeRepository;
import com.yeo_li.yeol_post.domain.feed.repository.FeedRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import com.yeo_li.yeol_post.global.logging.StructuredLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;

    public List<FeedResponse> getFeeds(OAuth2User principal) {
        Long userId = getUserId(principal);
        if (userId == null) {
            List<Feed> feeds = feedRepository.findAccessibleFeeds(
                ContentAccessLevel.PUBLIC.accessibleLevels());

            return convertFeedResponseListNoViewer(feeds);
        }

        User viewer = userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(FeedExceptionType.FEED_USER_NOT_FOUND));

        List<Feed> feeds = feedRepository.findAccessibleFeeds(
            viewer.getContentAccessLevel().accessibleLevels());

        return convertFeedResponseList(feeds, viewer);
    }

    private Long getUserId(OAuth2User principal) {
        if (principal == null) {
            return null;
        }

        Object userIdAttribute = principal.getAttributes().get("userId");
        if (userIdAttribute == null) {
            return null;
        }

        if (userIdAttribute instanceof Number userIdNumber) {
            return userIdNumber.longValue();
        }

        if (userIdAttribute instanceof String userIdString) {
            try {
                return Long.parseLong(userIdString);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private List<FeedResponse> convertFeedResponseList(List<Feed> feeds, User viewer) {
        List<Feed> activeFeeds = filterActiveFeeds(feeds);
        FeedLikeContext likeContext = getFeedLikeContext(activeFeeds, viewer.getId());
        List<FeedResponse> feedResponses = new ArrayList<>();
        for (Feed feed : activeFeeds) {
            feedResponses.add(convertFeedResponse(feed, viewer, likeContext));
        }
        return feedResponses;
    }

    private List<Feed> filterActiveFeeds(List<Feed> feeds) {
        List<Feed> activeFeeds = new ArrayList<>();
        for (Feed feed : feeds) {
            if (feed.getDeletedAt() == null) {
                activeFeeds.add(feed);
            }
        }
        return activeFeeds;
    }

    private FeedResponse convertFeedResponse(Feed feed, User user, FeedLikeContext likeContext) {
        return new FeedResponse(feed.getId(), feed.getAuthor().getNickname(), feed.getContent(),
            feed.getRequiredAccessLevel(),
            feed.getCreatedAt(), Objects.equals(feed.getAuthor().getId(), user.getId()),
            likeContext.countOf(feed.getId()), likeContext.isLiked(feed.getId()));
    }

    private List<FeedResponse> convertFeedResponseListNoViewer(List<Feed> feeds) {
        List<Feed> activeFeeds = filterActiveFeeds(feeds);
        FeedLikeContext likeContext = getFeedLikeContext(activeFeeds, null);
        List<FeedResponse> feedResponses = new ArrayList<>();
        for (Feed feed : activeFeeds) {
            feedResponses.add(convertFeedResponseNoViewer(feed, likeContext));
        }
        return feedResponses;
    }

    private FeedResponse convertFeedResponseNoViewer(Feed feed, FeedLikeContext likeContext) {
        return new FeedResponse(feed.getId(), feed.getAuthor().getNickname(), feed.getContent(),
            feed.getRequiredAccessLevel(), feed.getCreatedAt(), false,
            likeContext.countOf(feed.getId()), false);
    }

    @Transactional
    public FeedResponse saveFeed(OAuth2User principal, FeedCreateRequest request) {
        User author = getAuthenticatedUser(principal);

        if (request == null || request.requiredAccessLevel() == null) {
            throw new GeneralException(FeedExceptionType.FEED_ACCESS_LEVEL_INVALID);
        }

        Feed feed = new Feed();
        feed.setContent(sanitizeFeedContent(request.content()));
        feed.setRequiredAccessLevel(request.requiredAccessLevel());
        feed.setAuthor(author);

        Feed savedFeed = feedRepository.save(feed);
        log.info(StructuredLog.event(
                "FEED_CREATED",
                "피드가 생성되었습니다.",
                "CREATED"
            )
            .field("feedId", savedFeed.getId())
            .field("userId", author.getId())
            .field("requiredAccessLevel", savedFeed.getRequiredAccessLevel())
            .build());

        return convertFeedResponse(savedFeed, author, FeedLikeContext.empty());
    }

    @Transactional
    public FeedResponse updateFeed(OAuth2User principal, Long feedId, FeedUpdateRequest request) {
        User author = getAuthenticatedUser(principal);

        Feed feed = getActiveFeed(feedId);

        validateOwner(feed, author);

        if (request == null || (request.content() == null && request.requiredAccessLevel() == null)) {
            throw new GeneralException(FeedExceptionType.FEED_UPDATE_REQUEST_INVALID);
        }

        if (request.content() != null) {
            feed.setContent(sanitizeFeedContent(request.content()));
        }

        if (request.requiredAccessLevel() != null) {
            feed.setRequiredAccessLevel(request.requiredAccessLevel());
        }

        log.info(StructuredLog.event(
                "FEED_UPDATED",
                "피드 정보가 수정되었습니다.",
                "UPDATED"
            )
            .field("feedId", feed.getId())
            .field("userId", author.getId())
            .field("requiredAccessLevel", feed.getRequiredAccessLevel())
            .build());

        return convertFeedResponse(feed, author, getFeedLikeContext(List.of(feed), author.getId()));
    }

    @Transactional
    public void deleteFeed(OAuth2User principal, Long feedId) {
        User author = getAuthenticatedUser(principal);

        Feed feed = getActiveFeed(feedId);

        validateOwner(feed, author);

        feed.setDeletedAt(LocalDateTime.now());

        log.info(StructuredLog.event(
                "FEED_DELETED",
                "피드가 삭제 상태로 전환되었습니다.",
                "DELETED"
            )
            .field("feedId", feed.getId())
            .field("userId", author.getId())
            .build());
    }

    @Transactional
    public void likeFeed(OAuth2User principal, Long feedId) {
        User viewer = getAuthenticatedUser(principal);

        Feed feed = getActiveFeed(feedId);

        validateAccessible(feed, viewer);

        int inserted = feedLikeRepository.insertIgnore(viewer.getId(), feed.getId());
        if (inserted == 0) {
            return;
        }

        log.info(StructuredLog.event(
                "FEED_LIKED",
                "피드 좋아요가 반영되었습니다.",
                "APPLIED"
            )
            .field("feedId", feed.getId())
            .field("userId", viewer.getId())
            .field("feedOwnerUserId", feed.getAuthor().getId())
            .build());
    }

    @Transactional
    public void unlikeFeed(OAuth2User principal, Long feedId) {
        User viewer = getAuthenticatedUser(principal);

        Feed feed = getActiveFeed(feedId);

        validateAccessible(feed, viewer);

        if (!feedLikeRepository.existsByFeedIdAndUserId(feed.getId(), viewer.getId())) {
            return;
        }

        feedLikeRepository.deleteByFeedIdAndUserId(feed.getId(), viewer.getId());

        log.info(StructuredLog.event(
                "FEED_UNLIKED",
                "피드 좋아요 취소가 반영되었습니다.",
                "APPLIED"
            )
            .field("feedId", feed.getId())
            .field("userId", viewer.getId())
            .build());
    }

    private User getAuthenticatedUser(OAuth2User principal) {
        Long userId = getUserId(principal);
        if (userId == null) {
            throw new GeneralException(FeedExceptionType.FEED_USER_ID_INVALID);
        }

        return userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(FeedExceptionType.FEED_USER_NOT_FOUND));
    }

    private Feed getActiveFeed(Long feedId) {
        return feedRepository.findByIdAndDeletedAtIsNull(feedId)
            .orElseThrow(() -> new GeneralException(FeedExceptionType.FEED_NOT_FOUND));
    }

    private void validateOwner(Feed feed, User viewer) {
        if (!Objects.equals(feed.getAuthor().getId(), viewer.getId())) {
            throw new GeneralException(FeedExceptionType.FEED_FORBIDDEN);
        }
    }

    private void validateAccessible(Feed feed, User viewer) {
        if (!viewer.getContentAccessLevel().accessibleLevels()
            .contains(feed.getRequiredAccessLevel())) {
            throw new GeneralException(FeedExceptionType.FEED_FORBIDDEN);
        }
    }

    private FeedLikeContext getFeedLikeContext(List<Feed> feeds, Long viewerId) {
        if (feeds.isEmpty()) {
            return FeedLikeContext.empty();
        }

        List<Long> feedIds = feeds.stream()
            .map(Feed::getId)
            .toList();

        Map<Long, FeedLikeCount> countByFeedId = feedLikeRepository.countByFeedIds(feedIds)
            .stream()
            .collect(Collectors.toMap(FeedLikeCount::feedId, Function.identity()));

        Set<Long> likedFeedIds = viewerId == null
            ? Collections.emptySet()
            : new HashSet<>(feedLikeRepository.findLikedFeedIds(viewerId, feedIds));

        return new FeedLikeContext(countByFeedId, likedFeedIds);
    }

    private record FeedLikeContext(
        Map<Long, FeedLikeCount> countByFeedId,
        Set<Long> likedFeedIds
    ) {

        private static FeedLikeContext empty() {
            return new FeedLikeContext(Collections.emptyMap(), Collections.emptySet());
        }

        private long countOf(Long feedId) {
            FeedLikeCount count = countByFeedId.get(feedId);
            if (count == null) {
                return 0L;
            }
            return count.likeCount();
        }

        private boolean isLiked(Long feedId) {
            return likedFeedIds.contains(feedId);
        }
    }

    private String sanitizeFeedContent(String content) {
        if (content == null) {
            throw new GeneralException(FeedExceptionType.FEED_CONTENT_INVALID);
        }

        String sanitized = Jsoup.clean(
            content,
            "",
            Safelist.none(),
            new Document.OutputSettings().prettyPrint(false)
        );
        if (sanitized.isBlank()) {
            throw new GeneralException(FeedExceptionType.FEED_CONTENT_INVALID);
        }
        return sanitized;
    }
}
