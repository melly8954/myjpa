package com.melly.myjpa.controller;

import com.melly.myjpa.common.IResponseController;
import com.melly.myjpa.config.auth.PrincipalDetails;
import com.melly.myjpa.dto.ChangePasswordRequestDto;
import com.melly.myjpa.dto.MailRequest;
import com.melly.myjpa.common.ResponseDto;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.UserDto;
import com.melly.myjpa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController implements IResponseController {

    private final UserService userService;

    @PostMapping("/users/register")
    public ResponseEntity<ResponseDto> signUp(@Validated @RequestBody UserDto userDto, BindingResult bindingResult) {
        //  유효성 검사는 데이터가 비즈니스 로직에 들어가기 전에 검증하는 것
        if (bindingResult.hasErrors()) {
            // 유효성 검사를 실패(@Size , @NotBlank 조건 실패 시) 하면 BindingResult 객체에 오류가 담긴다.
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append(" / ");
            });
            return makeResponseEntity(HttpStatus.BAD_REQUEST,errorMessages.toString(), null);
        }
        try{ // 비즈니스 로직 시작
            UserEntity userEntity = this.userService.registerUser(userDto);
            return makeResponseEntity(HttpStatus.OK,"회원 가입 성공!", userEntity);
        }catch (IllegalArgumentException e) {
            // 닉네임 중복과 같은 비즈니스 로직 오류 처리
            log.error(e.getMessage());
            return makeResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), null); // 400 Bad Request
        }catch (Exception e){
            log.error(e.getMessage());
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류 : " + e.getMessage(),null);
        }
    }

    // 로그인 ID 중복 체크 API
    @PostMapping("/users/check-loginId")
    public ResponseEntity<Boolean> checkLoginId(@RequestBody UserDto userDto) {
        boolean isLoginIdExist = userService.isLoginIdExist(userDto.getLoginId());
        return ResponseEntity.ok(isLoginIdExist);
    }

    // 닉네임 중복 체크 API
    @PostMapping("/users/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestBody UserDto userDto) {
        boolean isNicknameExist = userService.isNicknameExist(userDto.getNickname());
        return ResponseEntity.ok(isNicknameExist);
    }

    // 이메일 중복 체크 API
    @PostMapping("/users/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestBody MailRequest emailRequest) {
        boolean isEmailExist = userService.isEmailExist(emailRequest.getMail());
        return ResponseEntity.ok(isEmailExist);
    }

//    // Spring Security 덕분에 구현할 필요가 없음 --> .loginProcessingUrl("/api/auth/login") 이걸로 요청을 가로챔
//    @PostMapping("/auth/login")
//    public ResponseEntity<ResponseDto> login(@Validated @RequestBody LoginRequestDto loginRequestDto, BindingResult bindingResult) {
//        if(bindingResult.hasErrors()) {
//            StringBuilder errorMessages = new StringBuilder();
//            bindingResult.getAllErrors().forEach(error -> {
//                errorMessages.append(error.getDefaultMessage()).append(" / ");
//            });
//            return makeResponseEntity(HttpStatus.BAD_REQUEST, "로그인 실패" + errorMessages.toString(), null);
//        }
//        try{
//            Boolean login = this.userService.login(loginRequestDto);
//            if(login){
//                return makeResponseEntity(HttpStatus.OK,"로그인 성공",login);
//            }else{
//                return makeResponseEntity(HttpStatus.BAD_REQUEST,"로그인 실패",null);
//            }
//        }catch(Exception e) {
//            log.error(e.getMessage());
//            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"로그인 실패" + e.getMessage(),null);
//        }
//    }

    @GetMapping("/users/login-id/{email}")
    public ResponseEntity<ResponseDto> findLoginIdByEmail(@PathVariable String email) {
        // 이메일이 null 이거나 비어 있는 경우 처리
        if(email == null || email.isEmpty()){
            return makeResponseEntity(HttpStatus.BAD_REQUEST,"email 은 필수 입력 항목입니다.",null);
        }

        // 이메일 형식의 정규표현식을 검사해주는 부분
        if(!email.matches("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")){
            return makeResponseEntity(HttpStatus.BAD_REQUEST,"email 형식을 맞추셔야 합니다.",null);
        }

        try{
            // 이메일로 로그인 ID를 조회
            String loginIdByEmail = this.userService.findLoginIdByEmail(email);
            return makeResponseEntity(HttpStatus.OK,"로그인 아이디 찾기 성공",loginIdByEmail);
        } catch (IllegalStateException e) {
            // 사용자가 없을 경우 404 Not Found 응답
            return makeResponseEntity(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            // 예상치 못한 예외 처리
            log.error("서버 오류", e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류 : " + e.getMessage(),null);
        }
    }

    // 로그인 상태에서 비밀번호 변경
    @PatchMapping("/users/password")
    public ResponseEntity<ResponseDto> changePassword(@Validated @RequestBody ChangePasswordRequestDto changePasswordRequestDto, BindingResult bindingResult) {
        try {
            // 유효성 검사 실패 시
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessages = new StringBuilder();
                bindingResult.getAllErrors().forEach(error -> {
                    errorMessages.append(error.getDefaultMessage()).append(" / ");
                });
                return makeResponseEntity(HttpStatus.BAD_REQUEST,errorMessages.toString(), null);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우
            }

            // 로그인한 사용자의 이메일 가져오기
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            String email = principalDetails.getEmail(); // 이메일 가져오기

            // 현재 비밀번호와 신규 비밀번호 처리
            String currentPassword = changePasswordRequestDto.getCurrentPassword();
            String newPassword = changePasswordRequestDto.getNewPassword();

            // 비밀번호 검증 (예: 현재 비밀번호 확인 및 새 비밀번호 저장)
            boolean passwordValid = userService.checkCurrentPassword(email, currentPassword);
            if (!passwordValid) {
                return makeResponseEntity(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.", null);
            }

            // 새 비밀번호로 업데이트
            userService.updatePassword(email, newPassword);

            return makeResponseEntity(HttpStatus.OK,"비밀번호 변경 완료",true);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류: " + ex.getMessage(), null);
        }
    }

    // 사용자 페이지에서 스스로 계정을 탈퇴 (삭제 처리)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable Long id) {
        try {
            if(id == null || id <= 0) {
                return makeResponseEntity(HttpStatus.BAD_REQUEST,"회원 ID > 0 을 만족해야 한다.",null);
            }
            this.userService.softDeleteUser(id);
            return makeResponseEntity(HttpStatus.OK,"해당 유저의 계정이 삭제되었습니다.",true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류: " + e.getMessage(),null);
        }
    }
}
