package com.melly.myjpa.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    @ResponseBody
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 서버에서 리디렉션을 하지 않고, JSON 응답으로 성공 메시지 전달
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        // 사용자 권한 확인 후 필요한 정보를 클라이언트에게 전달
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            response.getWriter().write("{\"redirectUrl\": \"/admin\"}");
        } else {
            response.getWriter().write("{\"redirectUrl\": \"/\"}");
        }
    }

}
