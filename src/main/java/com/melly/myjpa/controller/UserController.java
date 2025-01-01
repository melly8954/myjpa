package com.melly.myjpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/login")
    public String registerForm() {
        return "login_security/login_form";
    }

    @GetMapping("/sign-up")
    public String signUpForm() {
        return "login_security/sign_up_form";
    }
}
