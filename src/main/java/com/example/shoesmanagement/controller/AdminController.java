package com.example.shoesmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class AdminController {

    @Autowired
    private HttpSession session;

    @RequestMapping(value = {"", "/", "/home"})
    public String hienThi(Model model) {
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "/login";
        }
        System.out.println("aaaaa");
        return "manage/activities";
    }
}
