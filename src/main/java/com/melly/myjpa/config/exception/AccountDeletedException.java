package com.melly.myjpa.config.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountDeletedException extends AuthenticationException {
    public AccountDeletedException(String msg) {
        super(msg);
    }
}
