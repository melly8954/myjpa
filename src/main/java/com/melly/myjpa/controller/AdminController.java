package com.melly.myjpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("")
    public String findAdminPage() {
        return "/admin/admin_page";
    }

    @GetMapping("/users-form")
    public String findAllUsersForm() {
        return "admin/find_all_users";
    }
}
