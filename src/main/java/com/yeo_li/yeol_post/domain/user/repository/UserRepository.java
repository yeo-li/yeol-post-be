package com.yeo_li.yeol_post.domain.user.repository;

import com.yeo_li.yeol_post.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(String kakaoId);

    User findUserByNickname(String nickname);

    User findUserByKakaoId(String kakaoId);
}
