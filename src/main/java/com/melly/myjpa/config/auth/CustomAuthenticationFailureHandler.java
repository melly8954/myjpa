package com.melly.myjpa.config.auth;

import com.melly.myjpa.config.exception.AccountDeletedException;
import com.melly.myjpa.config.exception.CustomBadCredentialsException;
import com.melly.myjpa.config.exception.CustomDisabledException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMessage = "";

        if (exception instanceof BadCredentialsException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof CustomDisabledException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof AccountDeletedException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다.";
        }

        // JSON 응답으로 실패 메시지 전송
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + errorMessage + "\"}");
    }
}