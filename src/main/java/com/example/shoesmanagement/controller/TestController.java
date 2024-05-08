package com.example.shoesmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class TestController {
    @GetMapping("/test")
    public String test(){
        return "manage/test";
    }
}
