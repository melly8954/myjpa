package com.melly.myjpa.config.auth;

import com.melly.myjpa.config.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMessage = getErrorMessage(exception);

        if (exception instanceof OAuth2AccountDeletedException || exception instanceof OAuth2DisabledException) {
            response.sendRedirect("/login-fail?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
        } else {
            // JSON 응답을 위한 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"" + errorMessage + "\"}");
        }
    }

    private String getErrorMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return exception.getMessage();
        } else if (exception instanceof CustomDisabledException || exception instanceof AccountDeletedException) {
            return exception.getMessage();
        } else if (exception instanceof UsernameNotFoundException) {
            return "존재하지 않는 계정입니다.";
        } else if (exception instanceof OAuth2AccountDeletedException || exception instanceof OAuth2DisabledException) {
            return getOAuth2ErrorMessage(exception);
        }
        return "알 수 없는 오류가 발생했습니다.";  // 기본 메시지
    }

    private String getOAuth2ErrorMessage(AuthenticationException exception) {
        if (exception instanceof OAuth2AccountDeletedException) {
            OAuth2AccountDeletedException oauthException = (OAuth2AccountDeletedException) exception;
            return String.valueOf(oauthException.getError());
        } else if (exception instanceof OAuth2DisabledException) {
            OAuth2DisabledException oauthException = (OAuth2DisabledException) exception;
            return String.valueOf(oauthException.getError());
        }
        return "알 수 없는 OAuth2 오류";
    }
}
