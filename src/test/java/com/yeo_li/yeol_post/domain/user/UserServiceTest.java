package com.yeo_li.yeol_post.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.yeo_li.yeol_post.domain.user.domain.Role;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.dto.UserUpdateRequest;
import com.yeo_li.yeol_post.domain.user.exception.UserExceptionType;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private UserService userService;

    @Nested
    class UpdateUserTest {

        @Test
        void updateUser_사용자가_존재하지_않으면_리소스없음_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-1"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-1")).thenReturn(null);

            UserUpdateRequest request = new UserUpdateRequest("nick", "user@test.com", true);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(ErrorStatus.RESOURCE_NOT_FOUND));
        }

        @Test
        void updateUser_온보딩이_미완료이고_필수값이_존재하면_온보딩정보를_초기화한다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-2"));

            User user = createUser("kakao-2");
            user.setOnboardingCompletedAt(null);
            user.setNickname("oldNick");
            user.setEmail(null);
            user.setEmailOptIn(false);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-2")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("newNick", "new@test.com", true);

            // when
            userService.updateUser(principal, request);

            // then
            assertThat(user.getNickname()).isEqualTo("newNick");
            assertThat(user.getEmail()).isEqualTo("new@test.com");
            assertThat(user.getEmailOptIn()).isTrue();
            assertThat(user.getOnboardingCompletedAt()).isNotNull();
        }

        @Test
        void updateUser_온보딩중_이메일이_없으면_온보딩검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-3"));

            User user = createUser("kakao-3");
            user.setOnboardingCompletedAt(null);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-3")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("nick", null, true);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_ONBOARDING_INVALID));
        }

        @Test
        void updateUser_온보딩중_닉네임이_없으면_온보딩검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-5"));

            User user = createUser("kakao-5");
            user.setOnboardingCompletedAt(null);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-5")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest(null, "user@test.com", true);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_ONBOARDING_INVALID));
        }

        @Test
        void updateUser_온보딩중_이메일수신여부가_없으면_온보딩검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-6"));

            User user = createUser("kakao-6");
            user.setOnboardingCompletedAt(null);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-6")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("nick", "user@test.com", null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_ONBOARDING_INVALID));
        }

        @Test
        void updateUser_principal에_id가_없으면_인증식별자누락_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("sub", "no-id"));

            UserUpdateRequest request = new UserUpdateRequest("nick", "user@test.com", true);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_OAUTH2_ID_MISSING));
        }

        @Test
        void updateUser_principal이_null이면_NullPointerException을_발생시킨다() {
            // given
            UserUpdateRequest request = new UserUpdateRequest("nick", "user@test.com", true);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(null, request))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void updateUser_요청값이_null이면_업데이트검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-7"));

            User user = createUser("kakao-7");
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-7")).thenReturn(user);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, null))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_UPDATE_INVALID));
        }

        @Test
        void updateUser_이메일이_형식에_맞지_않으면_이메일검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-8"));

            User user = createUser("kakao-8");
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-8")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest(null, "invalid-email", null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_EMAIL_INVALID));
        }

        @Test
        void updateUser_닉네임이_공백이면_닉네임검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-9"));

            User user = createUser("kakao-9");
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-9")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("   ", null, null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_NICKNAME_INVALID));
        }

        @Test
        void updateUser_닉네임이_10자를_초과하면_닉네임검증_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-10"));

            User user = createUser("kakao-10");
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-10")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("12345678901", null, null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_NICKNAME_INVALID));
        }

        @Test
        void updateUser_닉네임이_중복되면_중복닉네임_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-11"));

            User user = createUser("kakao-11");
            user.setId(1L);
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-11")).thenReturn(user);

            User duplicatedUser = createUser("kakao-12");
            duplicatedUser.setId(2L);
            when(userRepository.findUserByNicknameAndDeletedAtIsNull("dupNick")).thenReturn(
                duplicatedUser);

            UserUpdateRequest request = new UserUpdateRequest("dupNick", null, null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(principal, request))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_NICKNAME_DUPLICATED));
        }

        @Test
        void updateUser_온보딩이_완료되면_null이_아닌_필드만_수정한다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-4"));

            User user = createUser("kakao-4");
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            user.setNickname("oldNick");
            user.setEmail("old@test.com");
            user.setEmailOptIn(false);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-4")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("newNick", null, null);

            // when
            userService.updateUser(principal, request);

            // then
            assertThat(user.getNickname()).isEqualTo("newNick");
            assertThat(user.getEmail()).isEqualTo("old@test.com");
            assertThat(user.getEmailOptIn()).isFalse();
        }

        @Test
        void updateUser_온보딩이_완료되면_닉네임과_이메일의_양끝공백을_제거하고_저장한다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-12"));

            User user = createUser("kakao-12");
            user.setOnboardingCompletedAt(LocalDateTime.now().minusDays(1));
            user.setNickname("oldNick");
            user.setEmail("old@test.com");
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-12")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("  newNick  ", "  new@test.com  ",
                null);

            // when
            userService.updateUser(principal, request);

            // then
            assertThat(user.getNickname()).isEqualTo("newNick");
            assertThat(user.getEmail()).isEqualTo("new@test.com");
        }

        @Test
        void updateUser_온보딩이_미완료면_닉네임과_이메일의_양끝공백을_제거하고_초기화한다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-13"));

            User user = createUser("kakao-13");
            user.setOnboardingCompletedAt(null);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-13")).thenReturn(user);

            UserUpdateRequest request = new UserUpdateRequest("  newNick  ", "  new@test.com  ",
                true);

            // when
            userService.updateUser(principal, request);

            // then
            assertThat(user.getNickname()).isEqualTo("newNick");
            assertThat(user.getEmail()).isEqualTo("new@test.com");
            assertThat(user.getEmailOptIn()).isTrue();
            assertThat(user.getOnboardingCompletedAt()).isNotNull();
        }
    }

    @Nested
    class DeleteUserTest {

        @Test
        void deleteUser_사용자가_존재하면_deletedAt을_설정한다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-delete-1"));

            User user = createUser("kakao-delete-1");
            user.setDeletedAt(null);
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-delete-1")).thenReturn(
                user);

            // when
            userService.deleteUser(principal);

            // then
            assertThat(user.getDeletedAt()).isNotNull();
        }

        @Test
        void deleteUser_사용자가_존재하지_않으면_사용자없음_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("id", "kakao-delete-2"));
            when(userRepository.findUserByKakaoIdAndDeletedAtIsNull("kakao-delete-2")).thenReturn(
                null);

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(principal))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_NOT_FOUND));
        }

        @Test
        void deleteUser_principal에_id가_없으면_인증식별자누락_예외를_발생시킨다() {
            // given
            when(principal.getAttributes()).thenReturn(Map.of("sub", "no-id"));

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(principal))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                    .isEqualTo(UserExceptionType.USER_OAUTH2_ID_MISSING));
        }

        @Test
        void deleteUser_principal이_null이면_NullPointerException을_발생시킨다() {
            // when & then
            assertThatThrownBy(() -> userService.deleteUser(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    private User createUser(String kakaoId) {
        User user = new User();
        user.setKakaoId(kakaoId);
        user.setName("tester");
        user.setNickname("nick");
        user.setRole(Role.USER);
        return user;
    }
}
