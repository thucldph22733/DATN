package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manage")
public class KhachHangController {

    @GetMapping("/khach-hang")
    public String dsKhachHang() {

        return "manage/khach-hang-test";
    }
}
