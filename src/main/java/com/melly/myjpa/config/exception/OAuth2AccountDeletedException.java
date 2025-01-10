package com.melly.myjpa.config.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2AccountDeletedException extends OAuth2AuthenticationException {

    // 생성자에서 메시지를 상위 클래스에 전달
    public OAuth2AccountDeletedException(String msg) {
        super(msg);  // 예외 메시지를 상위 클래스에 전달
    }
}
