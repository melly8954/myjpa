package com.melly.myjpa.controller;

import com.melly.myjpa.config.SecurityConfig;
import com.melly.myjpa.config.auth.PrincipalDetails;
import com.melly.myjpa.domain.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(Model model) {
        // SecurityContext에서 인증 객체를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof PrincipalDetails principalDetails) {
                // PrincipalDetails에서 UserEntity를 가져옴
                UserEntity loginUser = principalDetails.getUser();
                model.addAttribute(SecurityConfig.LOGINUSER, loginUser);
                model.addAttribute("isAuthenticated", true);
            } else if (principal instanceof DefaultOAuth2User oauthUser) {
                // OAuth2 사용자의 경우 (OAuth2UserService로 설정한 사용자 정보에서 추출)
                String nickname = oauthUser.getAttribute("nickname");
                model.addAttribute(SecurityConfig.LOGINUSER, nickname);
                model.addAttribute("isAuthenticated", true);
            } else {
                // 인증되지 않은 사용자
                model.addAttribute("isAuthenticated", false);
            }
        } else {
            // 인증 객체가 없거나 인증되지 않은 경우
            model.addAttribute("isAuthenticated", false);
        }
        return "index";
    }
}
