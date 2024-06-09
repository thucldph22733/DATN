package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.KhuyenMai;
import com.example.shoesmanagement.service.HoaDonService;
import com.example.shoesmanagement.service.KhachHangService;
import com.example.shoesmanagement.service.KhuyenMaiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/khuyen-mai")
    public String hienThi(Model model) {
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        List<KhachHang> khachHang = khachHangService.getAllKhachHang();
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("addKhuyenMai", new KhuyenMai());
        model.addAttribute("khachHang", khachHang);
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
return "redirect:/login";
        }
        return "manage/khuyen-mai";
    }

    @PostMapping("/khuyen-mai/add")
    public String addKhuyenMai(@ModelAttribute("addKhuyenMai") KhuyenMai km, Model model) throws ParseException {
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

    @PutMapping("khuyen-mai")
    public void capNhatSoLuong(HoaDon hoaDon){
        List<KhuyenMai> khuyenMai = (List<KhuyenMai>) hoaDon.getKhuyenMai();
        for (KhuyenMai km : khuyenMai){
            km.setSoLuong(km.getSoLuong() - hoaDon.getTongSP());
            km.setSoLuongDaDung(km.getSoLuongDaDung() + hoaDon.getTongSP());
            khuyenMaiService.save(km);
        }
    }
}
