package com.melly.myjpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/sign-up")
    public String signUpForm() {
        return "login_security/sign_up_form";
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
}
