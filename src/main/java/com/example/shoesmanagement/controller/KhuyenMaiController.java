package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.KhuyenMai;
import com.example.shoesmanagement.service.KhachHangService;
import com.example.shoesmanagement.service.KhuyenMaiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/manage")
public class KhuyenMaiController {
    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private HttpSession session;

    @GetMapping("/khuyen-mai")
    public String hienThi(Model model) {
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        List<KhachHang> khachHang = khachHangService.getAllKhachHang();
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("addKhuyenMai", new KhuyenMai());
        model.addAttribute("khachHang", khachHang);
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "/login";
        }
        return "manage/khuyen-mai";
    }

    @PostMapping("/khuyen-mai/add")
    public String addKhuyenMai(@ModelAttribute("addKhuyenMai") KhuyenMai km, Model model) throws ParseException {
//        KhuyenMai khuyenMai = new KhuyenMai();
//        khuyenMai.setIdKM(km.getIdKM());
//        khuyenMai.setMaKM(km.getMaKM());
//        khuyenMai.setTenKM(km.getTenKM());
//        khuyenMai.setTgBatDau(km.getTgBatDau());
//        khuyenMai.setTgKetThuc(km.getTgKetThuc());

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        khuyenMai.setTgBatDau(sdf.parse(String.valueOf(km.getTgBatDau())));
//
//        khuyenMai.setTgKetThuc(sdf.parse(String.valueOf(km.getTgKetThuc())));
//        khuyenMai.setSoLuong(km.getSoLuong());
        soSanh(km);
        khuyenMaiService.save(km);
        return "redirect:/manage/khuyen-mai";
    }

    @GetMapping("/khuyen-mai/detail/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        KhuyenMai km = khuyenMaiService.getByIdKhuyenMai(id);
        model.addAttribute("updateKhuyenMai", km);
        return "/manage/update-khuyen-mai";
    }

    @PostMapping("/khuyen-mai/update/{id}")
    public String update(@PathVariable UUID id, @ModelAttribute("updateKhuyenMai") KhuyenMai km, Model model) {
        soSanh(km);
        khuyenMaiService.save(km);
        return "redirect:/manage/khuyen-mai";
    }

    private void soSanh(KhuyenMai km) {
        Date today = new Date();

        if (today.before(km.getTgBatDau())) {
            km.setTrangThai(2);
        } else if (today.after(km.getTgBatDau()) && today.before(km.getTgKetThuc())) {
            km.setTrangThai(1);
        } else {
            km.setTrangThai(0);
        }
    }

}
