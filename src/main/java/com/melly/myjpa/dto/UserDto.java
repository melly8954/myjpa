package com.melly.myjpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDto {
    @NotBlank(message = "로그인 ID 항목은 필수 입력 항목입니다.")
    @Size(min = 5, max = 20, message = "로그인 ID는 5~20자 사이 입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호 항목은 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이 입니다.")
    private String password;

    @NotBlank(message = "이름 항목은 필수 입력 항목입니다.")
    @Size(min = 2, max = 100, message = "이름은 2~100자 사이 입니다.")
    private String name;

    @NotBlank(message = "닉네임 항목은 필수 입력 항목입니다.")
    @Size(min = 5, max = 30, message = "닉네임은 5~30자 사이 입니다.")
    private String nickname;

    @NotBlank(message = "email 항목은 필수 입력 항목입니다.")
    @Size(min = 10, max = 255, message = "email은 10~255자 사이 입니다.")
    private String email;
}
