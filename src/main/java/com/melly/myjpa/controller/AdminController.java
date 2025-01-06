package com.melly.myjpa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin-page")
    public String findAdminPage() {
        return "/admin/admin_page";
    }

    @GetMapping("/admin/users-form")
    public String findAllUsersForm() {
        return "admin/find_all_users";
    }
}
