package com.melly.myjpa.controller;

import com.melly.myjpa.common.MailRequest;
import com.melly.myjpa.common.MailVerificationRequest;
import com.melly.myjpa.service.MailService;
import com.melly.myjpa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@EnableAsync
public class MailApiController {
    private final MailService mailService;
    private final UserService userService;

    // 이메일 중복 체크 API
    @PostMapping("/api/users/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestBody MailRequest emailRequest) {
        boolean isEmailExist = userService.isEmailExist(emailRequest.getMail());
        return ResponseEntity.ok(isEmailExist);
    }



    /**
     * 인증번호 발송 메소드
     */
    @PostMapping("/api/users/mail")
    public CompletableFuture<String> mailSend(@RequestBody MailRequest mailRequest) {
        return mailService.sendMail(mailRequest.getMail())
                .thenApply(number -> String.valueOf(number));
    }

    /**
     * 인증번호 검증 메소드
     */
    @PostMapping("/api/users/verify-code")
    public String verifyCode(@RequestBody MailVerificationRequest verificationRequest) {
        boolean isVerified = mailService.verifyCode(verificationRequest.getMail(), verificationRequest.getCode());
        return isVerified ? "Verified" : "Verification failed";
    }
}

