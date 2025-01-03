package com.melly.myjpa.controller;

import com.melly.myjpa.common.IResponseController;
import com.melly.myjpa.dto.UserPageResponseDto;
import com.melly.myjpa.common.ResponseDto;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.LoginRequestDto;
import com.melly.myjpa.dto.UserDto;
import com.melly.myjpa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"회원가입 실패 : " + e.getMessage(),null);
        }
    }

//    // Spring Security 덕분에 구현할 필요가 없음
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
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 관리자에게 문의 : " + e.getMessage(),null);
        }
    }

    // 모든 사용자 조회
    // 컨트롤러에서 페이지 번호와 크기를 받아서 처리
    @GetMapping("/admin/users")
    public ResponseEntity<ResponseDto> getAllUsers(@RequestParam int page, @RequestParam int size) {
        // 페이지 번호가 1 이상인지 확인
        if (page < 1) {
            return makeResponseEntity(HttpStatus.BAD_REQUEST, "페이지 번호는 1 이상이어야 합니다.", null);
        }

        // 페이지 크기 제한 (예: 최대 100)
        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(page - 1, size);  // 1-based index를 0-based로 변환

        try {
            Page<UserEntity> allUsers = this.userService.getAllUsers(pageable);

            // 페이징 정보 포함하여 응답
            return makeResponseEntity(HttpStatus.OK, "회원 목록 조회 성공",
                    new UserPageResponseDto<>(allUsers.getContent(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.getNumber()+1, allUsers.getSize()));
        } catch (Exception e) {
            log.error("서버 오류: ", e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", null);
        }
    }
}
