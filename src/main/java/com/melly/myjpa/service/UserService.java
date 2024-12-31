package com.melly.myjpa.service;

import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.UserDto;
import com.melly.myjpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserDto userDto) {
        // 중복 체크
        if (userRepository.existsByLoginId(userDto.getLoginId())) {
            // 서비스에서 예외를 던지는 방식은 비즈니스 로직에 문제가 생겼을 때 이를 명확하게 클라이언트에 전달하는 좋은 방법
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID 입니다.");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
        }

        if (userRepository.existsByNickname(userDto.getNickname())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }



        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // UserEntity 객체 생성 및 저장 (빌더 패턴 사용)
        UserEntity user = UserEntity.builder()
                .loginId(userDto.getLoginId())
                .password(encodedPassword)
                .name(userDto.getName())
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .build();

        return userRepository.save(user);  // 저장 후 반환
    }

}
