package com.melly.myjpa.repository;

import com.melly.myjpa.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // 이메일 또는 로그인 ID로 중복 검사
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    // ID로 사용자 찾기
    Optional<UserEntity> findById(Long id);

    // 로그인 ID로 사용자 찾기
    UserEntity findByLoginId(String loginId);

    // 이름으로 사용자 찾기
    Page<UserEntity> findByName(String name, Pageable pageable);

    // 이메일로 사용자 찾기
    UserEntity findByEmail(String email);

    // 활성 사용자만 조회 (삭제되지 않은 사용자)
    @Query("SELECT u FROM UserEntity u WHERE u.deleteFlag = false")
    List<UserEntity> findAllActiveUsers();

}
