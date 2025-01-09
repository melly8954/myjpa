package com.melly.myjpa.config.exception;

import org.springframework.security.authentication.DisabledException;

public class CustomDisabledException extends DisabledException {
    public CustomDisabledException(String msg) {
        super(msg);
    }
}
