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
    // ID 가 없을 경우 null 이므로 Optional 적용이 된다.
    Optional<UserEntity> findById(Long id);

    // 로그인 ID로 사용자 찾기
    // 일반적으로 로그인 ID는 유일하므로 존재하지 않을 경우 예외를 던지거나 null을 반환하는 것이 일반적
    UserEntity findByLoginId(String loginId);

    // 이름으로 사용자 찾기
    Page<UserEntity> findByName(String name, Pageable pageable);

    // 이메일로 사용자 찾기
    UserEntity findByEmail(String email);

}
