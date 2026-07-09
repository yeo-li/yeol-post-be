package com.yeo_li.yeol_post.domain.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.feed.dto.request.FeedCreateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.request.FeedUpdateRequest;
import com.yeo_li.yeol_post.domain.feed.dto.response.FeedResponse;
import com.yeo_li.yeol_post.domain.feed.entity.Feed;
import com.yeo_li.yeol_post.domain.feed.exception.FeedExceptionType;
import com.yeo_li.yeol_post.domain.feed.repository.FeedRepository;
import com.yeo_li.yeol_post.domain.user.domain.Role;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.entity.ContentAccessLevel;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private FeedService feedService;

    @Nested
    class GetFeedsTest {

        @Test
        void getFeeds_로그인하지않은사용자는_PUBLIC_피드만_반환한다() {
            // given
            List<ContentAccessLevel> accessLevels = List.of(ContentAccessLevel.PUBLIC);
            User author = createUser(1L, ContentAccessLevel.PRIVATE);
            Feed publicFeed = createFeed(1L, "PUBLIC 피드", ContentAccessLevel.PUBLIC, author);

            when(feedRepository.findAccessibleFeeds(accessLevels))
                .thenReturn(List.of(publicFeed));

            // when
            List<FeedResponse> responses = feedService.getFeeds(null);

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses)
                .extracting(FeedResponse::requiredAccessLevel)
                .containsExactly(ContentAccessLevel.PUBLIC);
            assertThat(responses.get(0).isOwner()).isFalse();

            verify(feedRepository).findAccessibleFeeds(accessLevels);
            verifyNoInteractions(userRepository);
        }

        @Test
        void getFeeds_PUBLIC_사용자는_PUBLIC_피드만_반환한다() {
            // given
            List<ContentAccessLevel> accessLevels = List.of(ContentAccessLevel.PUBLIC);
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            Feed publicFeed = createFeed(1L, "PUBLIC 피드", ContentAccessLevel.PUBLIC, viewer);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findAccessibleFeeds(accessLevels))
                .thenReturn(List.of(publicFeed));

            // when
            List<FeedResponse> responses = feedService.getFeeds(principal);

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses)
                .extracting(FeedResponse::requiredAccessLevel)
                .containsExactly(ContentAccessLevel.PUBLIC);
            assertThat(responses.get(0).isOwner()).isTrue();

            verify(feedRepository).findAccessibleFeeds(accessLevels);
        }

        @Test
        void getFeeds_LIMITED_사용자는_PUBLIC_LIMITED_피드를_반환한다() {
            // given
            List<ContentAccessLevel> accessLevels = List.of(
                ContentAccessLevel.PUBLIC,
                ContentAccessLevel.LIMITED
            );
            User viewer = createUser(10L, ContentAccessLevel.LIMITED);
            User author = createUser(20L, ContentAccessLevel.PUBLIC);
            Feed publicFeed = createFeed(1L, "PUBLIC 피드", ContentAccessLevel.PUBLIC, author);
            Feed limitedFeed = createFeed(2L, "LIMITED 피드", ContentAccessLevel.LIMITED, viewer);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findAccessibleFeeds(accessLevels))
                .thenReturn(List.of(limitedFeed, publicFeed));

            // when
            List<FeedResponse> responses = feedService.getFeeds(principal);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses)
                .extracting(FeedResponse::requiredAccessLevel)
                .containsExactly(ContentAccessLevel.LIMITED, ContentAccessLevel.PUBLIC);
            assertThat(responses)
                .extracting(FeedResponse::isOwner)
                .containsExactly(true, false);

            verify(feedRepository).findAccessibleFeeds(accessLevels);
        }

        @Test
        void getFeeds_PRIVATE_사용자는_PUBLIC_LIMITED_PRIVATE_피드를_반환한다() {
            // given
            List<ContentAccessLevel> accessLevels = List.of(
                ContentAccessLevel.PUBLIC,
                ContentAccessLevel.LIMITED,
                ContentAccessLevel.PRIVATE
            );
            User viewer = createUser(10L, ContentAccessLevel.PRIVATE);
            User author = createUser(20L, ContentAccessLevel.PUBLIC);
            Feed publicFeed = createFeed(1L, "PUBLIC 피드", ContentAccessLevel.PUBLIC, author);
            Feed limitedFeed = createFeed(2L, "LIMITED 피드", ContentAccessLevel.LIMITED, author);
            Feed privateFeed = createFeed(3L, "PRIVATE 피드", ContentAccessLevel.PRIVATE, viewer);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findAccessibleFeeds(accessLevels))
                .thenReturn(List.of(privateFeed, limitedFeed, publicFeed));

            // when
            List<FeedResponse> responses = feedService.getFeeds(principal);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses)
                .extracting(FeedResponse::requiredAccessLevel)
                .containsExactly(
                    ContentAccessLevel.PRIVATE,
                    ContentAccessLevel.LIMITED,
                    ContentAccessLevel.PUBLIC
                );
            assertThat(responses)
                .extracting(FeedResponse::isOwner)
                .containsExactly(true, false, false);

            verify(feedRepository).findAccessibleFeeds(accessLevels);
        }

        @Test
        void getFeeds_삭제된_피드는_반환하지않는다() {
            // given
            List<ContentAccessLevel> accessLevels = List.of(
                ContentAccessLevel.PUBLIC,
                ContentAccessLevel.LIMITED,
                ContentAccessLevel.PRIVATE
            );
            User viewer = createUser(10L, ContentAccessLevel.PRIVATE);
            User author = createUser(20L, ContentAccessLevel.PUBLIC);
            Feed activeFeed = createFeed(1L, "노출 피드", ContentAccessLevel.PUBLIC, author);
            Feed deletedFeed = createFeed(2L, "삭제된 피드", ContentAccessLevel.PRIVATE, author);
            deletedFeed.setDeletedAt(LocalDateTime.now());

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findAccessibleFeeds(accessLevels))
                .thenReturn(List.of(deletedFeed, activeFeed));

            // when
            List<FeedResponse> responses = feedService.getFeeds(principal);

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses)
                .extracting(FeedResponse::feedId)
                .containsExactly(1L);
            assertThat(responses)
                .extracting(FeedResponse::content)
                .doesNotContain("삭제된 피드");

            verify(feedRepository).findAccessibleFeeds(accessLevels);
        }
    }

    @Nested
    class SaveFeedTest {

        @Test
        void saveFeed_유효한요청이면_피드를_저장하고_응답을_반환한다() {
            // given
            User author = createUser(10L, ContentAccessLevel.PRIVATE);
            FeedCreateRequest request = new FeedCreateRequest(
                "<script>alert('xss')</script>오늘의 기록",
                ContentAccessLevel.LIMITED
            );

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));
            when(feedRepository.save(any(Feed.class))).thenAnswer(invocation -> {
                Feed feed = invocation.getArgument(0);
                feed.setId(100L);
                return feed;
            });

            // when
            FeedResponse response = feedService.saveFeed(principal, request);

            // then
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);
            verify(feedRepository).save(feedCaptor.capture());

            Feed savedFeed = feedCaptor.getValue();
            assertThat(savedFeed.getContent()).isEqualTo("오늘의 기록");
            assertThat(savedFeed.getRequiredAccessLevel()).isEqualTo(ContentAccessLevel.LIMITED);
            assertThat(savedFeed.getAuthor()).isEqualTo(author);
            assertThat(savedFeed.getDeletedAt()).isNull();

            assertThat(response.feedId()).isEqualTo(100L);
            assertThat(response.authorNickname()).isEqualTo("닉네임10");
            assertThat(response.content()).isEqualTo("오늘의 기록");
            assertThat(response.requiredAccessLevel()).isEqualTo(ContentAccessLevel.LIMITED);
            assertThat(response.isOwner()).isTrue();
        }

        @Test
        void saveFeed_공백과_줄바꿈을_유지한다() {
            // given
            User author = createUser(10L, ContentAccessLevel.PRIVATE);
            String content = "  첫 줄\n\n  둘째  줄  ";
            FeedCreateRequest request = new FeedCreateRequest(
                content,
                ContentAccessLevel.PUBLIC
            );

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));
            when(feedRepository.save(any(Feed.class))).thenAnswer(invocation -> {
                Feed feed = invocation.getArgument(0);
                feed.setId(100L);
                return feed;
            });

            // when
            FeedResponse response = feedService.saveFeed(principal, request);

            // then
            ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);
            verify(feedRepository).save(feedCaptor.capture());

            assertThat(feedCaptor.getValue().getContent()).isEqualTo(content);
            assertThat(response.content()).isEqualTo(content);
        }

        @Test
        void saveFeed_principal에_userId가_없으면_인증실패_예외를_발생시킨다() {
            // given
            FeedCreateRequest request = new FeedCreateRequest("피드 내용", ContentAccessLevel.PUBLIC);
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> feedService.saveFeed(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_USER_ID_INVALID));
            verify(feedRepository, never()).save(any(Feed.class));
        }

        @Test
        void saveFeed_사용자가_존재하지않으면_사용자없음_예외를_발생시킨다() {
            // given
            FeedCreateRequest request = new FeedCreateRequest("피드 내용", ContentAccessLevel.PUBLIC);
            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedService.saveFeed(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_USER_NOT_FOUND));
            verify(feedRepository, never()).save(any(Feed.class));
        }

        @Test
        void saveFeed_태그제거후내용이비면_내용유효성_예외를_발생시킨다() {
            // given
            User author = createUser(10L, ContentAccessLevel.PUBLIC);
            FeedCreateRequest request = new FeedCreateRequest(
                "<script>alert('xss')</script>",
                ContentAccessLevel.PUBLIC
            );

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));

            // when & then
            assertThatThrownBy(() -> feedService.saveFeed(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_CONTENT_INVALID));
            verify(feedRepository, never()).save(any(Feed.class));
        }

        @Test
        void saveFeed_requiredAccessLevel이_null이면_접근권한유효성_예외를_발생시킨다() {
            // given
            User author = createUser(10L, ContentAccessLevel.PUBLIC);
            FeedCreateRequest request = new FeedCreateRequest("피드 내용", null);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(author));

            // when & then
            assertThatThrownBy(() -> feedService.saveFeed(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_ACCESS_LEVEL_INVALID));
            verify(feedRepository, never()).save(any(Feed.class));
        }
    }

    @Nested
    class UpdateFeedTest {

        @Test
        void updateFeed_피드소유자면_피드를_수정하고_응답을_반환한다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PRIVATE);
            Feed feed = createFeed(100L, "기존 피드", ContentAccessLevel.PUBLIC, viewer);
            FeedUpdateRequest request = new FeedUpdateRequest(
                "<script>alert('xss')</script>수정된 피드",
                ContentAccessLevel.LIMITED
            );

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when
            FeedResponse response = feedService.updateFeed(principal, 100L, request);

            // then
            assertThat(feed.getContent()).isEqualTo("수정된 피드");
            assertThat(feed.getRequiredAccessLevel()).isEqualTo(ContentAccessLevel.LIMITED);

            assertThat(response.feedId()).isEqualTo(100L);
            assertThat(response.content()).isEqualTo("수정된 피드");
            assertThat(response.requiredAccessLevel()).isEqualTo(ContentAccessLevel.LIMITED);
            assertThat(response.isOwner()).isTrue();
        }

        @Test
        void updateFeed_공백과_줄바꿈을_유지한다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PRIVATE);
            Feed feed = createFeed(100L, "기존 피드", ContentAccessLevel.PUBLIC, viewer);
            String content = "  수정된 피드\n\n  다음 줄  ";
            FeedUpdateRequest request = new FeedUpdateRequest(content, null);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when
            FeedResponse response = feedService.updateFeed(principal, 100L, request);

            // then
            assertThat(feed.getContent()).isEqualTo(content);
            assertThat(response.content()).isEqualTo(content);
        }

        @Test
        void updateFeed_principal에_userId가_없으면_인증실패_예외를_발생시킨다() {
            // given
            FeedUpdateRequest request = new FeedUpdateRequest("수정된 피드", null);
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> feedService.updateFeed(principal, 100L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_USER_ID_INVALID));
            verify(feedRepository, never()).findByIdAndDeletedAtIsNull(100L);
        }

        @Test
        void updateFeed_피드가_존재하지않으면_피드없음_예외를_발생시킨다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            FeedUpdateRequest request = new FeedUpdateRequest("수정된 피드", null);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedService.updateFeed(principal, 100L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_NOT_FOUND));
        }

        @Test
        void updateFeed_피드소유자가_아니면_권한없음_예외를_발생시킨다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            User author = createUser(20L, ContentAccessLevel.PUBLIC);
            Feed feed = createFeed(100L, "기존 피드", ContentAccessLevel.PUBLIC, author);
            FeedUpdateRequest request = new FeedUpdateRequest("수정된 피드", null);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when & then
            assertThatThrownBy(() -> feedService.updateFeed(principal, 100L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_FORBIDDEN));
            assertThat(feed.getContent()).isEqualTo("기존 피드");
        }

        @Test
        void updateFeed_수정할값이_없으면_수정요청유효성_예외를_발생시킨다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            Feed feed = createFeed(100L, "기존 피드", ContentAccessLevel.PUBLIC, viewer);
            FeedUpdateRequest request = new FeedUpdateRequest(null, null);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when & then
            assertThatThrownBy(() -> feedService.updateFeed(principal, 100L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_UPDATE_REQUEST_INVALID));
        }

        @Test
        void updateFeed_태그제거후내용이비면_내용유효성_예외를_발생시킨다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            Feed feed = createFeed(100L, "기존 피드", ContentAccessLevel.PUBLIC, viewer);
            FeedUpdateRequest request = new FeedUpdateRequest("<script>alert('xss')</script>", null);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when & then
            assertThatThrownBy(() -> feedService.updateFeed(principal, 100L, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_CONTENT_INVALID));
            assertThat(feed.getContent()).isEqualTo("기존 피드");
        }
    }

    @Nested
    class DeleteFeedTest {

        @Test
        void deleteFeed_피드소유자면_deletedAt을_설정한다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            Feed feed = createFeed(100L, "삭제할 피드", ContentAccessLevel.PUBLIC, viewer);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when
            feedService.deleteFeed(principal, 100L);

            // then
            assertThat(feed.getDeletedAt()).isNotNull();
        }

        @Test
        void deleteFeed_principal에_userId가_없으면_인증실패_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-only"));

            // when & then
            assertThatThrownBy(() -> feedService.deleteFeed(principal, 100L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_USER_ID_INVALID));
            verify(feedRepository, never()).findByIdAndDeletedAtIsNull(100L);
        }

        @Test
        void deleteFeed_피드가_존재하지않으면_피드없음_예외를_발생시킨다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> feedService.deleteFeed(principal, 100L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_NOT_FOUND));
        }

        @Test
        void deleteFeed_피드소유자가_아니면_권한없음_예외를_발생시킨다() {
            // given
            User viewer = createUser(10L, ContentAccessLevel.PUBLIC);
            User author = createUser(20L, ContentAccessLevel.PUBLIC);
            Feed feed = createFeed(100L, "삭제할 피드", ContentAccessLevel.PUBLIC, author);

            when(principal.getAttributes()).thenReturn(Map.of("userId", 10L));
            when(userRepository.findById(10L)).thenReturn(Optional.of(viewer));
            when(feedRepository.findByIdAndDeletedAtIsNull(100L)).thenReturn(Optional.of(feed));

            // when & then
            assertThatThrownBy(() -> feedService.deleteFeed(principal, 100L))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(FeedExceptionType.FEED_FORBIDDEN));
            assertThat(feed.getDeletedAt()).isNull();
        }
    }

    private User createUser(Long userId, ContentAccessLevel contentAccessLevel) {
        User user = new User();
        user.setId(userId);
        user.setKakaoId("kakao-" + userId);
        user.setName("사용자" + userId);
        user.setNickname("닉네임" + userId);
        user.setEmail("user" + userId + "@test.com");
        user.setRole(Role.USER);
        user.setContentAccessLevel(contentAccessLevel);
        return user;
    }

    private Feed createFeed(
        Long feedId,
        String content,
        ContentAccessLevel requiredAccessLevel,
        User author
    ) {
        Feed feed = new Feed();
        feed.setId(feedId);
        feed.setContent(content);
        feed.setRequiredAccessLevel(requiredAccessLevel);
        feed.setAuthor(author);
        return feed;
    }
}
