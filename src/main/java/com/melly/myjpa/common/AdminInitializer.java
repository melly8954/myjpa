package com.melly.myjpa.common;

import com.melly.myjpa.domain.RoleEntity;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.repository.RoleRepository;
import com.melly.myjpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // "admin" 역할이 존재하는지 확인
        Optional<RoleEntity> adminRoleOptional = roleRepository.findByRole("ADMIN");
        RoleEntity adminRole;

        // "admin" 역할이 존재하면 가져오기
        // admin 역할이 없으면 추가
        adminRole = adminRoleOptional.orElseGet(
                () -> roleRepository.save(RoleEntity.builder().role("ADMIN").build()) );

        // "admin" 사용자가 이미 존재하는지 확인
        if (!userRepository.existsByLoginId("testidsuper")) {
            UserEntity admin = UserEntity.builder()
                    .loginId("testidsuper")
                    .password(passwordEncoder.encode("super1q2w3e4r!"))  // 기본 관리자 비밀번호
                    .name("administrator")
                    .nickname("administrator")
                    .email("superemail@naver.com")
                    .role(adminRole)
                    .build();
            userRepository.save(admin);  // 관리자 계정 추가
        }
    }
}