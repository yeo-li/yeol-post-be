package com.yeo_li.yeol_post.domain.like.service;

import com.yeo_li.yeol_post.domain.like.domain.Like;
import com.yeo_li.yeol_post.domain.like.dto.LikeResponse;
import com.yeo_li.yeol_post.domain.like.exception.LikeExceptionType;
import com.yeo_li.yeol_post.domain.like.repository.LikeRepository;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.repository.PostRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.code.resultCode.ErrorStatus;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    public LikeResponse getLikes(OAuth2User principal, Long postId) {
        if (postId == null) {
            throw new GeneralException(LikeExceptionType.LIKE_POST_ID_INVALID);
        }

        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);

        boolean isLiked = false;
        if (user != null) {
            isLiked = likeRepository.existsLikesByPostIdAndUserId(postId, user.getId());
        }

        Post post = postRepository.findPostById(postId);
        if (post == null) {
            throw new GeneralException(LikeExceptionType.LIKE_POST_NOT_FOUND);
        }

        int likes = likeRepository.countLikeByPostId(postId);

        return new LikeResponse(
            isLiked,
            likes
        );
    }

    @Transactional
    public void like(OAuth2User principal, Long postId) {
        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        boolean isLiked = likeRepository.existsLikesByPostIdAndUserId(postId, user.getId());

        if (isLiked) {
            return;
        }

        Post post = postRepository.findPostById(postId);
        if (post == null) {
            throw new GeneralException(LikeExceptionType.LIKE_POST_NOT_FOUND);
        }

        likeRepository.save(new Like(user, post));
    }

    @Transactional
    public void unlike(OAuth2User principal, Long postId) {
        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }

        boolean isLiked = likeRepository.existsLikesByPostIdAndUserId(postId, user.getId());

        if (!isLiked) {
            return;
        }
        likeRepository.deleteLikeByPostIdAndUserId(postId, user.getId());
    }

    private String getKakaoId(OAuth2User principal) {
        if (principal == null) {
            return null;
        }
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes.get("id") == null) {
            return null;
        }
        return String.valueOf(attributes.get("id"));
    }
}
