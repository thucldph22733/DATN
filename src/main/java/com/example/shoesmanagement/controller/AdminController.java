package com.example.shoesmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class AdminController {
    @RequestMapping(value = {"", "/", "/home"})
    public String hienThi(Model model) {
        System.out.println("aaaaa");
        return "manage/activities";
    }
}
