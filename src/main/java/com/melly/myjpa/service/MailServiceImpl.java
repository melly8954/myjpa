package com.melly.myjpa.service;

import com.melly.myjpa.domain.UserEntity;
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
            helper.setSubject("myjpa project : 이메일 인증번호 발송");
            String body = "<h2>myjpa project 입니다.<br>환영합니다!</h2><h3>아래의 인증번호를 입력하세요.</h3><h1>" + verificationCodes.get(mail) + "</h1><h3>감사합니다.</h3>";
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

    // -------------------------------
    /**
     * 임시 비밀번호 자동 생성 메서드
     */
    private static String generateRandomPassword() {
        int length = 8;
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    /**
     * 임시 비밀번호 전송
     */
    @Override
    public void sendTemporaryPasswordMail(String mail, String tempPassword) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(mail);
            helper.setSubject("myjpa projcet : 임시 비밀번호 발송");
            String body = "<h2>myjpa project 입니다.<br>환영합니다!</h2><p>아래의 임시 비밀번호를 사용하세요.</p><h1>" + tempPassword + "</h1><h3>반드시 비밀번호를 재설정하세요.</h3>";
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("임시 비밀번호 전송 오류", e);
        }
    }

    /**
     * 임시 비밀번호 생성 및 DB 업데이트
     */
    @Override
    public String createTemporaryPassword(String mail) {
        String tempPassword = generateRandomPassword();
        UserEntity user = userRepository.findByEmail(mail);
        if (user == null) {
            throw new IllegalArgumentException("User not found for mail: " + mail);
        }
        user.changePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        return tempPassword;
    }

    /**
     * 임시 비밀번호 검증
     */
    @Override
    public boolean verifyTemporaryPassword(String mail, String tempPassword) {
        UserEntity user = userRepository.findByEmail(mail);
        if (user == null) {
            throw new IllegalArgumentException("User not found for mail: " + mail);
        }
        return passwordEncoder.matches(tempPassword, user.getPassword());
    }

}
