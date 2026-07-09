package com.yeo_li.yeol_post.domain.feed.service;

import com.yeo_li.yeol_post.domain.feed.dto.request.FeedCreateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.request.FeedUpdateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.response.FeedResponse;
import com.yeo_li.yeol_post.domain.feed.entity.Feed;
import com.yeo_li.yeol_post.domain.feed.exception.FeedExceptionType;
import com.yeo_li.yeol_post.domain.feed.repository.FeedRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

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
        List<FeedResponse> feedResponses = new ArrayList<>();
        for (Feed feed : feeds) {
            if (feed.getDeletedAt() == null) {
                feedResponses.add(convertFeedResponse(feed, viewer));
            }
        }
        return feedResponses;
    }

    private FeedResponse convertFeedResponse(Feed feed, User user) {
        return new FeedResponse(feed.getId(), feed.getAuthor().getNickname(), feed.getContent(),
            feed.getRequiredAccessLevel(),
            feed.getCreatedAt(), Objects.equals(feed.getAuthor().getId(), user.getId()));
    }

    private List<FeedResponse> convertFeedResponseListNoViewer(List<Feed> feeds) {
        List<FeedResponse> feedResponses = new ArrayList<>();
        for (Feed feed : feeds) {
            if (feed.getDeletedAt() == null) {
                feedResponses.add(convertFeedResponseNoViewer(feed));
            }
        }
        return feedResponses;
    }

    private FeedResponse convertFeedResponseNoViewer(Feed feed) {
        return new FeedResponse(feed.getId(), feed.getAuthor().getNickname(), feed.getContent(),
            feed.getRequiredAccessLevel(), feed.getCreatedAt(), false);
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
        return convertFeedResponse(savedFeed, author);
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

        return convertFeedResponse(feed, author);
    }

    @Transactional
    public void deleteFeed(OAuth2User principal, Long feedId) {
        User author = getAuthenticatedUser(principal);
        Feed feed = getActiveFeed(feedId);
        validateOwner(feed, author);

        feed.setDeletedAt(LocalDateTime.now());
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
