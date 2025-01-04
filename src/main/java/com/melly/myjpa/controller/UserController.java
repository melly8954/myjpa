package com.melly.myjpa.controller;

import com.melly.myjpa.config.auth.PrincipalDetails;
import com.melly.myjpa.domain.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class UserController {
    @GetMapping("/sign-up")
    public String signUpForm() {
        return "signup_form";
    }

    @GetMapping("/login")
    public String registerForm() {
        return "login_security/login_form";
    }


    @GetMapping("/users/login-id")
    public String findLoginIdForm(String email) {
        return "/login_security/find_loginId";
    }

    @GetMapping("/admin/users-form")
    public String findAllUsersForm() {
        return "/login_security/find_all_users";
    }



    // -------------------------------------------------
    // 다음은 Spring Security 를 사용하여 인증 정보를 얻는 과정
    @GetMapping("/check-session1")
    @ResponseBody
    public String checkSession1(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    @GetMapping("/check-session2")
    @ResponseBody
    public UserEntity checkSession2(Authentication authentication) {
        // PrincipalDetails 클래스는 UserDetails 을 implements 한다.
        // Authentication 객체에는 2가지 타입을 가진다.
        // UserDetails 는 일반 로그인 , OAuth2User 는 구글 로그인
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return principalDetails.getUser();
    }

    @GetMapping("/check-session3")
    @ResponseBody
    public UserEntity checkSession3(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        // PrincipalDetails 클래스는 UserDetails 을 implements 한다.
        return userDetails.getUser();
    }

    // OAuth check( OAuth 로그인은 check1은 가능 but 2~3 은 안됨)
    @GetMapping("/check-session4")
    @ResponseBody
    public Map<String, Object> checkSession4(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        // 각 속성에 접근
//        String sub = (String) attributes.get("sub");
//        String name = (String) attributes.get("name");
//        String picture = (String) attributes.get("picture");
//        Boolean emailVerified = (Boolean) attributes.get("email_verified");
        return oAuth2User.getAttributes();
    }

    @GetMapping("/check-session5")
    @ResponseBody
    public Map<String, Object> checkSession5(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return oAuth2User.getAttributes();
    }
}
