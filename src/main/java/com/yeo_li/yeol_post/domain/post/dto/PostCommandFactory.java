package com.yeo_li.yeol_post.domain.post.dto;

import com.yeo_li.yeol_post.domain.category.Category;
import com.yeo_li.yeol_post.domain.category.CategoryRepository;
import com.yeo_li.yeol_post.domain.post.command.DraftPostCreateCommand;
import com.yeo_li.yeol_post.domain.post.command.PostCreateCommand;
import com.yeo_li.yeol_post.domain.post.domain.Post;
import com.yeo_li.yeol_post.domain.post.exception.PostExceptionType;
import com.yeo_li.yeol_post.domain.tag.TagRepository;
import com.yeo_li.yeol_post.domain.user.domain.User;
import com.yeo_li.yeol_post.domain.user.repository.UserRepository;
import com.yeo_li.yeol_post.global.common.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCommandFactory {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PostCreateCommand createPostCommand(OAuth2User principal, PostCreateRequest request) {
        Boolean isPublished = true;
        LocalDateTime publishedAt = LocalDateTime.now();

        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(PostExceptionType.USER_NOT_FOUND);
        }

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException(
                "PostCommandFactory:createPostCommand: Category의 Id가 올바르지 않습니다."));

        Boolean isDeleted = false;

        return new PostCreateCommand(
            request.title(),
            request.summary(),
            request.author(),
            request.content(),
            isPublished,
            publishedAt,
            user,
            category,
            request.tags(),
            isDeleted
        );
    }

    private String getKakaoId(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes.get("id") == null) {
            return null;
        }
        return String.valueOf(attributes.get("id"));
    }

    public DraftPostCreateCommand createDraftPostCommand(OAuth2User principal,
        PostCreateRequest request) {
        Boolean isPublished = false;

        String kakaoId = getKakaoId(principal);
        User user = userRepository.findUserByKakaoIdAndDeletedAtIsNull(kakaoId);
        if (user == null) {
            throw new GeneralException(PostExceptionType.USER_NOT_FOUND);
        }

        Category category = categoryRepository.findById(request.categoryId())
            .orElseGet(null);

        Boolean isDeleted = false;

        return new DraftPostCreateCommand(
            request.title(),
            request.summary(),
            request.author(),
            request.content(),
            isPublished,
            null,
            user,
            category,
            request.tags(),
            isDeleted
        );
    }

    public PostMailCommand createPostMailCommand(Post post) {
        return new PostMailCommand(post.getId(), post.getTitle(), post.getSummary());
    }
}
