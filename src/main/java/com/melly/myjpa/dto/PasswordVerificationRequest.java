package com.melly.myjpa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordVerificationRequest {
    private String mail;
    private String tempPassword;
}
