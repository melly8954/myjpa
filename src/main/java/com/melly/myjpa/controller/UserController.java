package com.melly.myjpa.controller;

import com.melly.myjpa.common.IResponseController;
import com.melly.myjpa.common.ResponseDto;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.UserDto;
import com.melly.myjpa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements IResponseController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> signUp(@Validated @RequestBody UserDto userDto, BindingResult bindingResult) {
        //  유효성 검사는 데이터가 비즈니스 로직에 들어가기 전에 검증하는 것
        if (bindingResult.hasErrors()) {
            // 유효성 검사를 실패(@Size , @NotBlank 조건 실패 시) 하면 BindingResult 객체에 오류가 담긴다.
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append(" / ");
            });
            return makeResponseEntity(HttpStatus.BAD_REQUEST, errorMessages.toString(), null);
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
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"관리자에게 문의 : " + e.getMessage(),null);
        }
    }
}
