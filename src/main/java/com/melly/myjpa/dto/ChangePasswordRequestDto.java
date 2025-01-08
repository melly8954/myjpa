package com.melly.myjpa.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ChangePasswordRequestDto {
    @NotBlank(message = "현재 비밀번호 항목은 필수 입력 항목입니다.")
    @Size(min = 5, max = 20, message = "비밀번호는 5~20자 사이 입니다.")
    private String currentPassword;

    @NotBlank(message = "신규 비밀번호 항목은 필수 입력 항목입니다.")
    @Size(min = 5, max = 20, message = "비밀번호는 5~20자 사이 입니다.")
    private String newPassword;

    @NotBlank(message = "신규 비밀번호 확인 항목은 필수 입력 항목입니다.")
    @Size(min = 5, max = 20, message = "비밀번호는 5~20자 사이 입니다.")
    private String confirmNewPassword;
}
