package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manage")
public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(1, "Hoạt động");
        dsTrangThai.put(0, "Không hoạt động");
        return dsTrangThai;
    }

    @ModelAttribute("dsGioiTinh")
    public Map<Integer, String> getDsGioiTinh() {
        Map<Integer, String> dsGioiTinh = new HashMap<>();
        dsGioiTinh.put(1, "Nam");
        dsGioiTinh.put(0, "Nữ");
        dsGioiTinh.put(2, "Khác");
        return dsGioiTinh;
    }

    @GetMapping("/khach-hang")
    public String dsKhachHang(Model model, @ModelAttribute("message") String message) {
        List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
        model.addAttribute("khachHang", khachHangs);
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        return "manage/khach-hang";
    }
}
