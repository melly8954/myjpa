package com.melly.myjpa.config.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class OAuth2DisabledException extends OAuth2AuthenticationException {
    public OAuth2DisabledException(String msg) {
        super(msg);
    }
}
