package com.yeo_li.yeol_post.domain.user.repository;

import com.yeo_li.yeol_post.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByKakaoId(String kakaoId);

    User findUserByNicknameAndDeletedAtIsNull(String nickname);

    User findUserByKakaoIdAndDeletedAtIsNull(String kakaoId);
}
