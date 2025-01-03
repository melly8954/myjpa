package com.melly.myjpa.service;

import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    MimeMessage createMail(String mail);

    boolean verifyCode(String email, int code);

    CompletableFuture<Integer> sendMail(String mail);

}
