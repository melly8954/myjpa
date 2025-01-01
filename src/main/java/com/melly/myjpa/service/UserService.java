package com.melly.myjpa.service;

import com.melly.myjpa.domain.RoleEntity;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.LoginRequestDto;
import com.melly.myjpa.dto.UserDto;
import com.melly.myjpa.repository.RoleRepository;
import com.melly.myjpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserDto userDto) {
        // 중복 체크
        if (userRepository.existsByLoginId(userDto.getLoginId())) {
            // 서비스에서 예외를 던지는 방식은 비즈니스 로직에 문제가 생겼을 때 이를 명확하게 클라이언트에 전달하는 좋은 방법
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID 입니다.");
        }
        if (userRepository.existsByNickname(userDto.getNickname())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
        }
        // 이메일 형식의 정규표현식을 검사해주는 부분
        if(!userDto.getEmail().matches("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")){
            throw new IllegalArgumentException("email 형식을 맞추셔야 합니다.");
        }

        // 비밀번호와 비밀번호 확인 일치 여부 확인
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        // 기본 'USER' 역할 할당 (관리자 등 역할은 추후 확장 가능)
        RoleEntity userRole = roleRepository.findByRole("USER")
                .orElseThrow(() -> new IllegalArgumentException("기본 역할이 존재하지 않습니다."));

        // UserEntity 객체 생성 및 저장 (빌더 패턴 사용)
        UserEntity user = UserEntity.builder()
                .loginId(userDto.getLoginId())
                .password(encodedPassword)
                .name(userDto.getName())
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .role(userRole)  // 기본 역할 할당
                .build();

        return userRepository.save(user);  // 저장 후 반환
    }

    public Boolean login(LoginRequestDto loginRequestDto){
        // 입력된 ID로 사용자 조회
        UserEntity user = userRepository.findByLoginId(loginRequestDto.getLoginId());

        if (user == null) {
            // ID에 해당하는 사용자가 없을 경우 false 반환
            return false;
        }
        // 비밀번호 확인 (입력된 비밀번호와 저장된 암호화된 비밀번호 비교)
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            return false;
        }
        return true;
    }

    public String findLoginIdByEmail(String email) {
        // 이메일에 해당하는 사용자를 조회
        UserEntity user = this.userRepository.findByEmail(email);

        // 사용자가 존재하지 않으면 예외를 던진다.
        if (user == null) {
            throw new IllegalStateException("해당 이메일로 등록된 사용자가 없습니다.");
        }

        // 사용자가 존재하면 로그인 ID 반환
        String loginId = user.getLoginId();
        return loginId;
    }
//    public String findLoginIdByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .map(UserEntity::getLoginId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 등록된 사용자가 없습니다."));
//    }

    // 모든 회원 정보 및 페이징 처리
    public Page<UserEntity> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
