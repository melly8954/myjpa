package com.melly.myjpa.service;

import com.melly.myjpa.domain.RoleEntity;
import com.melly.myjpa.domain.StatusType;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.LoginRequestDto;
import com.melly.myjpa.dto.UserDto;
import com.melly.myjpa.repository.RoleRepository;
import com.melly.myjpa.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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
        if (userRepository.existsByNickname(userDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
        }
        // 이메일 형식의 정규표현식을 검사해주는 부분
        if (!userDto.getEmail().matches("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")) {
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
                .statusType(StatusType.ACTIVE)
                .build();

        return userRepository.save(user);  // 저장 후 반환
    }

    // 로그인ID 중복 여부 확인
    public boolean isLoginIdExist(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 이메일 중복 여부 확인
    public boolean isNicknameExist(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 이메일 중복 여부 확인
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean login(LoginRequestDto loginRequestDto) {
        // 입력된 ID로 사용자 조회
        UserEntity user = userRepository.findByLoginId(loginRequestDto.getLoginId());

        if (user == null) {
            // ID에 해당하는 사용자가 없을 경우 false 반환
            return false;
        }
        // 비밀번호 확인 (입력된 비밀번호와 저장된 암호화된 비밀번호 비교)
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            return false;
        }
        return true;
    }

    // 모든 회원 정보 및 페이징 처리
    public Page<UserEntity> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // ID 로 사용자 찾기
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // name 으로 회원정보 찾기
    public Page<UserEntity> getUserByName(String name, Pageable pageable) {
        return userRepository.findByName(name, pageable);
    }

    // 이메일로 로그인ID 찾기
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

    // 현재 비밀번호가 맞는지 검증
    public boolean checkCurrentPassword(String email, String currentPassword) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            return false; // 이메일에 해당하는 사용자가 없으면 false 반환
        }
        // BCryptPasswordEncoder로 암호화된 비밀번호 비교
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    // 새 비밀번호로 업데이트
    public void updatePassword(String email, String newPassword) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            // 새 비밀번호 암호화
            user.changePassword(passwordEncoder.encode(newPassword));
            userRepository.save(user); // 비밀번호 업데이트
        }
    }

    // 사용자 or 관리자가 자신의 계정을 탈퇴 (삭제 처리)
    @Transactional
    public void softDeleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // 사용자가 자신을 삭제할 때, 계정을 삭제된 상태로 변경
        user.changeStatus(StatusType.DELETED); // 상태를 DELETED로 변경
        user.changeDeleteDate(LocalDateTime.now()); // 삭제 날짜를 현재 시간으로 설정
        userRepository.save(user); // 변경 사항 저장
    }

    // 관리자가 계정을 비활성화
    @Transactional
    public void softDisableUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // 계정을 비활성화 상태로 변경
        user.changeStatus(StatusType.INACTIVE); // 상태를 INACTIVE로 변경
        user.changeDisableDate(LocalDateTime.now()); // 비활성화 날짜를 현재 시간으로 설정
        userRepository.save(user); // 변경 사항 저장
    }

    // 사용자 계정 복구
    @Transactional
    public void undoUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // 계정이 비활성화(인 inactive) 또는 삭제(deleted) 상태인 경우 복구
        if (user.getStatusType() == StatusType.INACTIVE || user.getStatusType() == StatusType.DELETED) {
            user.changeStatus(StatusType.ACTIVE); // 상태를 ACTIVE로 변경
            user.changeDeleteDate(null); // 비활성화 날짜 및 삭제 날짜 초기화
            user.changeDisableDate(null); // 비활성화 날짜 및 삭제 날짜 초기화
            userRepository.save(user); // 변경 사항 저장
        } else {
            throw new IllegalStateException("User is already active and cannot be undone.");
        }
    }
}
