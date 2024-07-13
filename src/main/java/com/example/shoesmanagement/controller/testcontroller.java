package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.service.HoaDonService;
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
public class testcontroller {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonService hoaDonService;

    @ModelAttribute("danhSachTrangThai")
    public Map<Integer, String> getDanhSach() {
        Map<Integer, String> danhSach = new HashMap<>();
        danhSach.put(1, "HD Thành công");
        danhSach.put(0, "HD Chờ");
        return danhSach;
    }

    @GetMapping("/test")
    public String hienThi(Model model) {
        List<HoaDon> hoaDons = hoaDonService.getAllHoaDonOffLine();
        model.addAttribute("listHD", hoaDons);
        return "manage/test";
    }

//    @GetMapping("/manage/test/loc")
//    public String filterData(@RequestParam(required = false) Integer trangThai, Model model) {
//
//    }
}
