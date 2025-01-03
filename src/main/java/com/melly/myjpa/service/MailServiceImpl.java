package com.melly.myjpa.service;

import com.melly.myjpa.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

// 1) 주어진 이메일 주소에 대해 6자리 인증 코드를 생성하고 verificationCodes 맵에 저장한다. {이메일 : 인증코드} 형태로 저장될 것이다.
// 2) 입력한 이메일 주소로 발송할 이메일 메시지를 작성한다.
// 3) 2에서 생성한 이메일 메시지를 비동기적으로 발송한다.
// 4) 사용자가 입력한 인증코드와 실제 발송된 인증코드와 일치하는지 확인한다.


@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "rkwhr8963@gmail.com";
    private static final Map<String, Integer> verificationCodes = new HashMap<>();

    /**
     * 인증 코드 자동 생성 메서드
     */
    public static void createNumber(String email){
        int number = new Random().nextInt(900000) + 100000; // 100000-999999 사이의 숫자 생성
        verificationCodes.put(email, number);
    }

    /**
     * 이메일 전송
     */
    @Override
    public MimeMessage createMail(String mail){
        createNumber(mail);
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("이메일 인증번호");
            String body = "<h2>000에 오신걸 환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + verificationCodes.get(mail) + "</h1><h3>감사합니다.</h3>";
            helper.setText(body, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    /**
     * createMail() 메서드의 내용을 이메일 전송
     */
    @Async
    @Override
    public CompletableFuture<Integer> sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
        return CompletableFuture.completedFuture(verificationCodes.get(mail));
    }

    /**
     * 이메일 인증 코드 검증
     */
    @Override
    public boolean verifyCode(String mail, int code) {
        Integer storedCode = verificationCodes.get(mail);
        return storedCode != null && storedCode == code;
    }

}
